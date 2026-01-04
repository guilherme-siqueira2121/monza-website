package com.monza.app.service;

import com.monza.app.domain.Board;
import com.monza.app.persistence.mapper.BoardMapper;
import com.monza.app.persistence.repository.BoardRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardMapper boardMapper;

     public BoardService(BoardRepository boardRepository, BoardMapper boardMapper) {
         this.boardRepository = boardRepository;
         this.boardMapper = boardMapper;
     }

     // list all available license plates
     public List<Board> findAllBoards() {
         return boardRepository.findAllByOrderByCreatedAtDesc()
                 .stream()
                 .map(boardMapper::toDomain)
                 .collect(Collectors.toList());
     }

     // search board by id
    public Optional<Board> findById(Long id) {
         return boardRepository.findById(id)
                 .map(boardMapper::toDomain);
    }
}

// -