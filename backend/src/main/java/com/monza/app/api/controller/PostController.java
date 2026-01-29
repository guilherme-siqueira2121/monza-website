package com.monza.app.api.controller;

import com.monza.app.api.dto.CreatePostRequest;
import com.monza.app.api.dto.ErrorResponse;
import com.monza.app.api.dto.PostResponse;
import com.monza.app.api.dto.UpdatePostRequest;
import com.monza.app.api.dto.UserResponse;
import com.monza.app.api.dto.VoteRequest;
import com.monza.app.api.dto.VoteResponse;
import com.monza.app.api.dto.NestedPostResponse;
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
                    buildPostResponse(post, null)
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/thread/{threadId}/nested")
    public ResponseEntity<List<NestedPostResponse>> getNestedPostsByThread(@PathVariable Long threadId,
                                                                           @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long uid = null;
        if (authHeader != null) {
            String token = extractTokenFromHeader(authHeader);
            try { uid = jwtService.extractUserId(token); } catch (Exception ignored) {}
        }
        final Long currentUserId = uid;

        List<NestedPostResponse> posts = postService.buildNestedPostResponses(threadId, currentUserId);

        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, 
                                       @RequestBody UpdatePostRequest request,
                                       @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            Long userId = jwtService.extractUserId(token);
            String userRole = jwtService.extractRole(token);

            Post updatedPost = postService.updatePost(id, userId, request.getContent(), userRole);
            return ResponseEntity.ok(buildPostResponse(updatedPost, userId));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Não autorizado"));
        }
    }

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

    @PostMapping("/{id}/vote")
    public ResponseEntity<?> votePost(@PathVariable("id") Long postId,
                                      @RequestBody VoteRequest request,
                                      @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            Long userId = jwtService.extractUserId(token);

            VoteResponse resp = postService.votePost(postId, userId, request.getValue());
            return ResponseEntity.ok().body(resp);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Não autorizado"));
        }
    }

    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Token não fornecido");
    }

    private PostResponse buildPostResponse(Post post, Long currentUserId) {
        User author = userService.findById(post.getUserId()).orElse(null);

        UserResponse authorResponse = author != null ?
                new UserResponse(author.getId(), author.getUsername(),
                        author.getUserCode(), author.getRole(), author.getCreatedAt()) : null;

        int up = postService.getUpvotes(post.getId());
        int down = postService.getDownvotes(post.getId());
        Integer currentVote = null;
        if (currentUserId != null) {
            currentVote = postService.getUserVoteForPost(post.getId(), currentUserId);
        }

        return new PostResponse(
                post.getId(),
                post.getContent(),
                authorResponse,
                post.getReplyToPostId(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                up,
                down,
                currentVote
        );
    }
}

// -