package com.monza.app.api.controller;

import com.monza.app.api.dto.CreatePostRequest;
import com.monza.app.api.dto.ErrorResponse;
import com.monza.app.api.dto.PostResponse;
import com.monza.app.api.dto.UpdatePostRequest;
import com.monza.app.api.dto.UserResponse;
import com.monza.app.domain.Post;
import com.monza.app.domain.User;
import com.monza.app.service.PostService;
import com.monza.app.service.UserService;
import com.monza.app.security.JwtService;
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
    private final JwtService jwtService;

    public PostController(PostService postService, UserService userService, JwtService jwtService) {
        this.postService = postService;
        this.userService = userService;
        this.jwtService = jwtService;
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

    // edit post
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, 
                                       @RequestBody UpdatePostRequest request,
                                       @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            Long userId = jwtService.extractUserId(token);
            String userRole = jwtService.extractRole(token);

            Post updatedPost = postService.updatePost(id, userId, request.getContent(), userRole);
            return ResponseEntity.ok(buildPostResponse(updatedPost));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Não autorizado"));
        }
    }

    // delete post
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id,
                                       @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            Long userId = jwtService.extractUserId(token);
            String userRole = jwtService.extractRole(token);

            postService.deletePost(id, userId, userRole);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Não autorizado"));
        }
    }

    // extract token from Authorization header
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Token não fornecido");
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
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}

// -