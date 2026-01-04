package com.monza.app.persistence.repository;

import com.monza.app.persistence.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByUserCode(String userCode);
    boolean existsByUsername(String username);
    boolean existsByUserCode(String userCode);
}

// -