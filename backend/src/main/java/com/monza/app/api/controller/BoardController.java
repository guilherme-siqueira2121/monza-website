package com.monza.app.api.controller;

import com.monza.app.api.dto.BoardResponse;
import com.monza.app.persistence.repository.ForumThreadRepository;
import com.monza.app.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/boards")
public class BoardController {
    private final BoardService boardService;
    private final ForumThreadRepository forumThreadRepository;

    public BoardController(BoardService boardService, ForumThreadRepository forumThreadRepository) {
        this.boardService = boardService;
        this.forumThreadRepository = forumThreadRepository;
    }

    @GetMapping
    public ResponseEntity<List<BoardResponse>> getAllBoards() {
        List<BoardResponse> boards = boardService.findAllBoards()
                .stream()
                .map(board -> new BoardResponse(
                        board.getId(),
                        board.getName(),
                        board.getTitle(),
                        board.getDescription(),
                        forumThreadRepository.countByBoardId(board.getId())
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(boards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBoardById(@PathVariable Long id) {
        return boardService.findById(id)
                .map(board -> {
                    BoardResponse response = new BoardResponse(
                            board.getId(),
                            board.getName(),
                            board.getTitle(),
                            board.getDescription(),
                            forumThreadRepository.countByBoardId(board.getId())
                    );

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

// -