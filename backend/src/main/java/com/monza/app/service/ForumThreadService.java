package com.monza.app.service;

import com.monza.app.domain.ForumThread;
import com.monza.app.persistence.entity.ForumThreadEntity;
import com.monza.app.persistence.mapper.ForumThreadMapper;
import com.monza.app.persistence.repository.BoardRepository;
import com.monza.app.persistence.repository.ForumThreadRepository;
import com.monza.app.persistence.repository.UserRepository;
import com.monza.app.service.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ForumThreadService {
    private final ForumThreadRepository forumThreadRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ForumThreadMapper forumThreadMapper;

    public ForumThreadService(ForumThreadRepository forumThreadRepository,
                              BoardRepository boardRepository,
                              UserRepository userRepository,
                              ForumThreadMapper forumThreadMapper) {
        this.forumThreadRepository = forumThreadRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.forumThreadMapper = forumThreadMapper;
    }

    @Transactional
    public ForumThread createThread(Long boardId, Long userId, String title, String content) {
        if (!boardRepository.existsById(boardId)) {
            throw new IllegalArgumentException("Board não existe");
        }
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Usuário não existe");
        }

        ForumThread thread = new ForumThread(boardId, userId, title, content);
        thread.validate();

        ForumThreadEntity entity = forumThreadMapper.toEntity(thread);
        ForumThreadEntity saved = forumThreadRepository.save(entity);

        return forumThreadMapper.toDomain(saved);
    }

    public List<ForumThread> findThreadsByBoard(Long boardId) {
        return forumThreadRepository.findThreadsByBoardId(boardId)
                .stream()
                .map(forumThreadMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<ForumThread> findById(Long id) {
        return forumThreadRepository.findById(id)
                .map(forumThreadMapper::toDomain);
    }

    public void updateThreadTimestamp(Long threadId) {
        forumThreadRepository.findById(threadId).ifPresent(entity -> {
            entity.setUpdatedAt(LocalDateTime.now());
            forumThreadRepository.save(entity);
        });
    }

    @Transactional
    public ForumThread togglePinThread(Long threadId, Long currentUserId, String currentUserRole, boolean pin) {
        if (!PermissionService.canPinThread(currentUserRole)) {
            throw new IllegalArgumentException("Sem permissão para fixar thread");
        }
        
        return forumThreadRepository.findById(threadId)
                .map(entity -> {
                    entity.setPinned(pin);
                    entity.setUpdatedAt(LocalDateTime.now());
                    ForumThreadEntity saved = forumThreadRepository.save(entity);
                    return forumThreadMapper.toDomain(saved);
                })
                .orElseThrow(() -> new IllegalArgumentException("Thread não encontrada"));
    }

    @Transactional
    public ForumThread toggleLockThread(Long threadId, Long currentUserId, String currentUserRole, boolean lock) {
        if (!PermissionService.canLockThread(currentUserRole)) {
            throw new IllegalArgumentException("Sem permissão para travar thread");
        }
        
        return forumThreadRepository.findById(threadId)
                .map(entity -> {
                    entity.setLocked(lock);
                    entity.setUpdatedAt(LocalDateTime.now());
                    ForumThreadEntity saved = forumThreadRepository.save(entity);
                    return forumThreadMapper.toDomain(saved);
                })
                .orElseThrow(() -> new IllegalArgumentException("Thread não encontrada"));
    }

    @Transactional
    public ForumThread updateThread(Long threadId, Long currentUserId, String currentUserRole, String title, String content) {
        ForumThreadEntity threadEntity = forumThreadRepository.findById(threadId)
                .orElseThrow(() -> new IllegalArgumentException("Thread não encontrada"));

        if (!PermissionService.canEditThread(threadEntity.getUserId(), currentUserId, currentUserRole)) {
            throw new IllegalArgumentException("Sem permissão para editar esta thread");
        }

        ForumThread thread = forumThreadMapper.toDomain(threadEntity);
        thread.setTitle(title);
        thread.setContent(content);
        thread.validate();

        threadEntity.setTitle(title);
        threadEntity.setContent(content);
        threadEntity.setUpdatedAt(LocalDateTime.now());
        ForumThreadEntity saved = forumThreadRepository.save(threadEntity);

        return forumThreadMapper.toDomain(saved);
    }

    @Transactional
    public void deleteThread(Long threadId, Long currentUserId, String currentUserRole) {
        ForumThreadEntity threadEntity = forumThreadRepository.findById(threadId)
                .orElseThrow(() -> new IllegalArgumentException("Thread não encontrada"));

        if (!PermissionService.canDeleteThread(threadEntity.getUserId(), currentUserId, currentUserRole)) {
            throw new IllegalArgumentException("Sem permissão para deletar esta thread");
        }

        forumThreadRepository.delete(threadEntity);
    }
}

// -