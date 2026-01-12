package com.monza.app.api.controller;

import com.monza.app.api.dto.CreateThreadRequest;
import com.monza.app.api.dto.ErrorResponse;
import com.monza.app.api.dto.ThreadResponse;
import com.monza.app.api.dto.UserResponse;
import com.monza.app.domain.ForumThread;
import com.monza.app.domain.User;
import com.monza.app.service.PostService;
import com.monza.app.service.ForumThreadService;
import com.monza.app.service.UserService;
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

    public ForumThreadController(ForumThreadService forumThreadService, UserService userService,
                                 PostService postService) {
        this.forumThreadService = forumThreadService;
        this.userService = userService;
        this.postService = postService;
    }

    // create new thread
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

    // list of threads on a board
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<ThreadResponse>> getThreadsByBoard(@PathVariable Long boardId) {
        List<ThreadResponse> threads = forumThreadService.findThreadsByBoard(boardId)
                .stream()
                .map(this::buildThreadResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(threads);
    }

    // looking for thread by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getThreadById(@PathVariable Long id) {
        return forumThreadService.findById(id)
                .map(thread -> ResponseEntity.ok(buildThreadResponse(thread)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/pin")
    public ResponseEntity<?> pinThread(@PathVariable Long id) {
        try {
            ForumThread thread = forumThreadService.togglePinThread(id, true);
            return ResponseEntity.ok(buildThreadResponse(thread));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Thread não encontrada"));
        }
    }

    @PatchMapping("/{id}/unpin")
    public ResponseEntity<?> unpinThread(@PathVariable Long id) {
        try {
            ForumThread thread = forumThreadService.togglePinThread(id, false);
            return ResponseEntity.ok(buildThreadResponse(thread));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Thread não encontrada"));
        }
    }

    @PatchMapping("/{id}/lock")
    public ResponseEntity<?> lockThread(@PathVariable Long id) {
        try {
            ForumThread thread = forumThreadService.toggleLockThread(id, true);
            return ResponseEntity.ok(buildThreadResponse(thread));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Thread não encontrada"));
        }
    }

    @PatchMapping("/{id}/unlock")
    public ResponseEntity<?> unlockThread(@PathVariable Long id) {
        try {
            ForumThread thread = forumThreadService.toggleLockThread(id, false);
            return ResponseEntity.ok(buildThreadResponse(thread));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Thread não encontrada"));
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
}

// -