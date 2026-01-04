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
}

// -