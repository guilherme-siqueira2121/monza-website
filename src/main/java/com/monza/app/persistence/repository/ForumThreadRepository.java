package com.monza.app.persistence.repository;

import com.monza.app.persistence.entity.ForumThreadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ForumThreadRepository extends JpaRepository<ForumThreadEntity, Long> {

    @Query("SELECT t FROM ForumThreadEntity t WHERE t.boardId = ?1 ORDER BY t.isPinned DESC, t.updatedAt DESC")
    List<ForumThreadEntity> findThreadsByBoardId(Long boardId);

    long countByBoardId(Long boardId);
}

// -