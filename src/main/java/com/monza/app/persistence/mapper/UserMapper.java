package com.monza.app.persistence.mapper;

import com.monza.app.domain.*;
import com.monza.app.persistence.entity.*;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // converts entity to domain
    public User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getUserCode(),
                entity.getCreatedAt()
        );
    }

    // converts domain to entity
    public UserEntity toEntity(User domain) {
        if (domain == null) return null;
        UserEntity entity = new UserEntity(
                domain.getUsername(),
                domain.getUserCode(),
                domain.getCreatedAt()
        );
        entity.setId(domain.getId());
        return entity;
    }
}

// -