package com.monza.app.api.controller;

import com.monza.app.api.dto.CreatePostRequest;
import com.monza.app.api.dto.ErrorResponse;
import com.monza.app.api.dto.PostResponse;
import com.monza.app.api.dto.UserResponse;
import com.monza.app.domain.Post;
import com.monza.app.domain.User;
import com.monza.app.service.PostService;
import com.monza.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    // create new post
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest request) {
        try {
            Post post = postService.createPost(
                    request.getThreadId(),
                    request.getUserId(),
                    request.getContent(),
                    request.getReplyToPostId()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    buildPostResponse(post)
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // list the posts in the thread
    @GetMapping("/thread/{threadId}")
    public ResponseEntity<List<PostResponse>> getPostsByThread(@PathVariable Long threadId) {
        List<PostResponse> posts = postService.findPostsByThread(threadId)
                .stream()
                .map(this::buildPostResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(posts);
    }

    // construct a PostResponse with related data
    private PostResponse buildPostResponse(Post post) {
        User author = userService.findById(post.getUserId()).orElse(null);

        UserResponse authorResponse = author != null ?
                new UserResponse(author.getId(), author.getUsername(),
                        author.getUserCode(), author.getRole(), author.getCreatedAt()) : null;

        return new PostResponse(
                post.getId(),
                post.getContent(),
                authorResponse,
                post.getReplyToPostId(),
                post.getCreatedAt()
        );
    }
}

// -