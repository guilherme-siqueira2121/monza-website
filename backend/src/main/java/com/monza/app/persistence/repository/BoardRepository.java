package com.monza.app.persistence.repository;

import com.monza.app.persistence.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    Optional<BoardEntity> findByName(String name);
    List<BoardEntity> findAllByOrderByCreatedAtDesc();
}

// -