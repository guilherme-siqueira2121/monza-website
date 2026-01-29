package com.monza.app.persistence.repository;

import com.monza.app.persistence.entity.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<VoteEntity, Long> {
    Optional<VoteEntity> findByPostIdAndUserId(Long postId, Long userId);

    @Query("SELECT COALESCE(SUM(v.value), 0) FROM VoteEntity v WHERE v.postId = :postId")
    int sumValueByPostId(@Param("postId") Long postId);

    @Query("SELECT COUNT(v) FROM VoteEntity v WHERE v.postId = :postId AND v.value = 1")
    int countUpvotesByPostId(@Param("postId") Long postId);

    @Query("SELECT COUNT(v) FROM VoteEntity v WHERE v.postId = :postId AND v.value = -1")
    int countDownvotesByPostId(@Param("postId") Long postId);

    void deleteByPostIdAndUserId(Long postId, Long userId);
}

// -