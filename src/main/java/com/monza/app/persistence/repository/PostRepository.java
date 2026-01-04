package com.monza.app.persistence.repository;

import com.monza.app.persistence.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query("SELECT p FROM PostEntity p WHERE p.threadId = ?1 ORDER BY p.createdAt ASC")
    List<PostEntity> findPostsByThread(Long threadId);

    long countByThreadId(Long threadId);
}

// -