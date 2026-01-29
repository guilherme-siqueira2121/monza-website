package com.monza.app.api.controller;

import com.monza.app.api.dto.CreateThreadRequest;
import com.monza.app.api.dto.ErrorResponse;
import com.monza.app.api.dto.ThreadResponse;
import com.monza.app.api.dto.UpdateThreadRequest;
import com.monza.app.api.dto.UserResponse;
import com.monza.app.domain.ForumThread;
import com.monza.app.domain.User;
import com.monza.app.service.PostService;
import com.monza.app.service.ForumThreadService;
import com.monza.app.service.UserService;
import com.monza.app.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/threads")
public class ForumThreadController {
    private final ForumThreadService forumThreadService;
    private final UserService userService;
    private final PostService postService;
    private final JwtService jwtService;

    public ForumThreadController(ForumThreadService forumThreadService, UserService userService,
                                 PostService postService, JwtService jwtService) {
        this.forumThreadService = forumThreadService;
        this.userService = userService;
        this.postService = postService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<?> createThread(@RequestBody CreateThreadRequest request) {
        try {
            ForumThread thread = forumThreadService.createThread(
                    request.getBoardId(),
                    request.getUserId(),
                    request.getTitle(),
                    request.getContent()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    buildThreadResponse(thread)
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse((e.getMessage())));
        }
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<ThreadResponse>> getThreadsByBoard(@PathVariable Long boardId) {
        List<ThreadResponse> threads = forumThreadService.findThreadsByBoard(boardId)
                .stream()
                .map(this::buildThreadResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(threads);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getThreadById(@PathVariable Long id) {
        return forumThreadService.findById(id)
                .map(thread -> ResponseEntity.ok(buildThreadResponse(thread)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/pin")
    public ResponseEntity<?> pinThread(@PathVariable Long id,
                                       @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            Long userId = jwtService.extractUserId(token);
            String userRole = jwtService.extractRole(token);

            ForumThread thread = forumThreadService.togglePinThread(id, userId, userRole, true);
            return ResponseEntity.ok(buildThreadResponse(thread));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Não autorizado"));
        }
    }

    @PatchMapping("/{id}/unpin")
    public ResponseEntity<?> unpinThread(@PathVariable Long id,
                                         @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            Long userId = jwtService.extractUserId(token);
            String userRole = jwtService.extractRole(token);

            ForumThread thread = forumThreadService.togglePinThread(id, userId, userRole, false);
            return ResponseEntity.ok(buildThreadResponse(thread));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Não autorizado"));
        }
    }

    @PatchMapping("/{id}/lock")
    public ResponseEntity<?> lockThread(@PathVariable Long id,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            Long userId = jwtService.extractUserId(token);
            String userRole = jwtService.extractRole(token);

            ForumThread thread = forumThreadService.toggleLockThread(id, userId, userRole, true);
            return ResponseEntity.ok(buildThreadResponse(thread));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Não autorizado"));
        }
    }

    @PatchMapping("/{id}/unlock")
    public ResponseEntity<?> unlockThread(@PathVariable Long id,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            Long userId = jwtService.extractUserId(token);
            String userRole = jwtService.extractRole(token);

            ForumThread thread = forumThreadService.toggleLockThread(id, userId, userRole, false);
            return ResponseEntity.ok(buildThreadResponse(thread));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Não autorizado"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateThread(@PathVariable Long id,
                                        @RequestBody UpdateThreadRequest request,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            Long userId = jwtService.extractUserId(token);
            String userRole = jwtService.extractRole(token);

            ForumThread thread = forumThreadService.updateThread(id, userId, userRole, request.getTitle(), request.getContent());
            return ResponseEntity.ok(buildThreadResponse(thread));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Não autorizado"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteThread(@PathVariable Long id,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            Long userId = jwtService.extractUserId(token);
            String userRole = jwtService.extractRole(token);

            forumThreadService.deleteThread(id, userId, userRole);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Não autorizado"));
        }
    }

    private ThreadResponse buildThreadResponse(ForumThread thread) {
        User author = userService.findById(thread.getUserId()).orElse(null);
        long postCount = postService.countPostsByThread(thread.getId());

        UserResponse authorResponse = author != null ?
                new UserResponse(author.getId(), author.getUsername(),
                        author.getUserCode(), author.getRole(), author.getCreatedAt()) : null;

        return new ThreadResponse(
                thread.getId(),
                thread.getTitle(),
                thread.getContent(),
                authorResponse,
                postCount,
                thread.isPinned(),
                thread.isLocked(),
                thread.getCreatedAt(),
                thread.getUpdatedAt()
        );
    }

    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Token não fornecido");
    }
}

// -