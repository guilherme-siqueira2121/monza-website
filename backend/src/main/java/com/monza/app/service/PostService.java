package com.monza.app.service;

import com.monza.app.api.dto.VoteResponse;
import com.monza.app.domain.Post;
import com.monza.app.domain.ForumThread;
import com.monza.app.persistence.entity.ForumThreadEntity;
import com.monza.app.persistence.entity.PostEntity;
import com.monza.app.persistence.entity.VoteEntity;
import com.monza.app.persistence.mapper.PostMapper;
import com.monza.app.persistence.repository.PostRepository;
import com.monza.app.persistence.repository.ForumThreadRepository;
import com.monza.app.persistence.repository.UserRepository;
import com.monza.app.persistence.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class PostService {
    private final PostRepository postRepository;
    private final ForumThreadRepository forumThreadRepository;
    private final UserRepository userRepository;
    private final ForumThreadService forumThreadService;
    private final PostMapper postMapper;
    private final VoteRepository voteRepository;

    public PostService(PostRepository postRepository,
                       ForumThreadRepository forumThreadRepository,
                       UserRepository userRepository,
                       ForumThreadService forumThreadService,
                       PostMapper postMapper,
                       VoteRepository voteRepository) {
        this.postRepository = postRepository;
        this.forumThreadRepository = forumThreadRepository;
        this.userRepository = userRepository;
        this.forumThreadService = forumThreadService;
        this.postMapper = postMapper;
        this.voteRepository = voteRepository;
    }

    @Transactional
    public Post createPost(Long threadId, Long userId, String content, Long replyToPostId) {
        ForumThreadEntity threadEntity = forumThreadRepository.findById(threadId)
                .orElseThrow(() -> new IllegalArgumentException("Thread não existe"));

        if (threadEntity.isLocked()) {
            throw new IllegalArgumentException("Thread está travada");
        }
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Usuário não existe");
        }
        if (replyToPostId != null && !postRepository.existsById(replyToPostId)) {
            throw new IllegalArgumentException("Post de reply não existe");
        }

        Post post = new Post(threadId, userId, content, replyToPostId);
        post.validate();

        PostEntity entity = postMapper.toEntity(post);
        PostEntity saved = postRepository.save(entity);

        forumThreadService.updateThreadTimestamp(threadId);

        return postMapper.toDomain(saved);
    }

    public List<Post> findPostsByThread(Long threadId) {
        return postRepository.findPostsByThread(threadId)
                .stream()
                .map(postMapper::toDomain)
                .collect(Collectors.toList());
    }

    public long countPostsByThread(Long threadId) {return postRepository.countByThreadId(threadId);}

    @Transactional
    public Post updatePost(Long postId, Long userId, String content, String userRole) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post não existe"));

        if (!canEditPost(postEntity, userId, userRole)) {
            throw new IllegalArgumentException("Sem permissão para editar este post");
        }

        Post post = postMapper.toDomain(postEntity);
        post.setContent(content);
        post.validate();

        postEntity.setContent(content);
        postEntity.setUpdatedAt(LocalDateTime.now());
        PostEntity saved = postRepository.save(postEntity);

        return postMapper.toDomain(saved);
    }

    @Transactional
    public void deletePost(Long postId, Long userId, String userRole) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post não existe"));

        // verify permission
        if (!canDeletePost(postEntity, userId, userRole)) {
            throw new IllegalArgumentException("Sem permissão para deletar este post");
        }

        postRepository.delete(postEntity);
    }

    @Transactional
    public VoteResponse votePost(Long postId, Long userId, int value) {
        if (value != 1 && value != -1) {
            throw new IllegalArgumentException("Valor de voto inválido");
        }
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("Post não existe");
        }
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Usuário não existe");
        }

        Optional<VoteEntity> existing = voteRepository.findByPostIdAndUserId(postId, userId);
        if (existing.isPresent()) {
            VoteEntity v = existing.get();
            if (v.getValue() == value) {
                voteRepository.deleteByPostIdAndUserId(postId, userId);
            } else {
                v.setValue((short) value);
                voteRepository.save(v);
            }
        } else {
            VoteEntity v = new VoteEntity();
            v.setPostId(postId);
            v.setUserId(userId);
            v.setValue((short) value);
            v.setCreatedAt(LocalDateTime.now());
            voteRepository.save(v);
        }

        int up = voteRepository.countUpvotesByPostId(postId);
        int down = voteRepository.countDownvotesByPostId(postId);
        Integer current = getUserVoteForPost(postId, userId);
        return new VoteResponse(up, down, current);
    }

    public int getPostScore(Long postId) {
        return voteRepository.sumValueByPostId(postId);
    }

    public int getUpvotes(Long postId) { return voteRepository.countUpvotesByPostId(postId); }
    public int getDownvotes(Long postId) { return voteRepository.countDownvotesByPostId(postId); }

    public Integer getUserVoteForPost(Long postId, Long userId) {
        return voteRepository.findByPostIdAndUserId(postId, userId)
                .map(VoteEntity::getValue)
                .map(Integer::valueOf)
                .orElse(null);
    }

    private boolean canEditPost(PostEntity post, Long userId, String userRole) {
        if ("ADMIN".equals(userRole)) {
            return true;
        }
        return post.getUserId().equals(userId);
    }

    private boolean canDeletePost(PostEntity post, Long userId, String userRole) {
        if ("ADMIN".equals(userRole)) {
            return true;
        }
        return post.getUserId().equals(userId);
    }
}

// -