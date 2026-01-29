package com.monza.app.persistence.mapper;

import com.monza.app.domain.*;
import com.monza.app.persistence.entity.*;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getUserCode(),
                entity.getPassword(),
                entity.getRole(),
                entity.isEnabled(),
                entity.getCreatedAt()
        );
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;

        UserEntity entity = new UserEntity(
                domain.getUsername(),
                domain.getUserCode(),
                domain.getPassword(),
                domain.getRole(),
                domain.isEnabled(),
                domain.getCreatedAt()
        );
        entity.setId(domain.getId());
        return entity;
    }
}

// -