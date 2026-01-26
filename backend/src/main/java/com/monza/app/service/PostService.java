package com.monza.app.service;

import com.monza.app.domain.Post;
import com.monza.app.domain.ForumThread;
import com.monza.app.persistence.entity.ForumThreadEntity;
import com.monza.app.persistence.entity.PostEntity;
import com.monza.app.persistence.mapper.PostMapper;
import com.monza.app.persistence.repository.PostRepository;
import com.monza.app.persistence.repository.ForumThreadRepository;
import com.monza.app.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class PostService {
    private final PostRepository postRepository;
    private final ForumThreadRepository forumThreadRepository;
    private final UserRepository userRepository;
    private final ForumThreadService forumThreadService;
    private final PostMapper postMapper;

    public PostService(PostRepository postRepository,
                       ForumThreadRepository forumThreadRepository,
                       UserRepository userRepository,
                       ForumThreadService forumThreadService,
                       PostMapper postMapper) {
        this.postRepository = postRepository;
        this.forumThreadRepository = forumThreadRepository;
        this.userRepository = userRepository;
        this.forumThreadService = forumThreadService;
        this.postMapper = postMapper;
    }

    // create a new post
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

        // persiste
        PostEntity entity = postMapper.toEntity(post);
        PostEntity saved = postRepository.save(entity);

        forumThreadService.updateThreadTimestamp(threadId);

        return postMapper.toDomain(saved);
    }

    // search post by thread
    public List<Post> findPostsByThread(Long threadId) {
        return postRepository.findPostsByThread(threadId)
                .stream()
                .map(postMapper::toDomain)
                .collect(Collectors.toList());
    }

    public long countPostsByThread(Long threadId) {return postRepository.countByThreadId(threadId);}

    // edit post
    @Transactional
    public Post updatePost(Long postId, Long userId, String content, String userRole) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post não existe"));

        // verify permission
        if (!canEditPost(postEntity, userId, userRole)) {
            throw new IllegalArgumentException("Sem permissão para editar este post");
        }

        // validate content
        Post post = postMapper.toDomain(postEntity);
        post.setContent(content);
        post.validate();

        // update
        postEntity.setContent(content);
        postEntity.setUpdatedAt(LocalDateTime.now());
        PostEntity saved = postRepository.save(postEntity);

        return postMapper.toDomain(saved);
    }

    // delete post
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

    // verify edit permission
    private boolean canEditPost(PostEntity post, Long userId, String userRole) {
        // admin can edit any post
        if ("ADMIN".equals(userRole)) {
            return true;
        }
        // user can only edit own posts
        return post.getUserId().equals(userId);
    }

    // verify delete permission
    private boolean canDeletePost(PostEntity post, Long userId, String userRole) {
        // admin can delete any post
        if ("ADMIN".equals(userRole)) {
            return true;
        }
        // user can only delete own posts
        return post.getUserId().equals(userId);
    }
}

// -