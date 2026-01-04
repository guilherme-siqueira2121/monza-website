package com.monza.app.persistence.mapper;

import com.monza.app.domain.ForumThread;
import com.monza.app.persistence.entity.*;
import org.springframework.stereotype.Component;

@Component
public class ForumThreadMapper {

    // converts entity to domain
    public ForumThread toDomain(ForumThreadEntity entity) {
        if (entity == null) return null;
        return new ForumThread(
                entity.getId(),
                entity.getBoardId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getContent(),
                entity.isPinned(),
                entity.isLocked(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    // converts domain to entity
    public ForumThreadEntity toEntity(ForumThread domain) {
        if (domain == null) return null;
        ForumThreadEntity entity = new ForumThreadEntity();
        entity.setId(domain.getId());
        entity.setBoardId(domain.getBoardId());
        entity.setUserId(domain.getUserId());
        entity.setTitle(domain.getTitle());
        entity.setContent(domain.getContent());
        entity.setPinned(domain.isPinned());
        entity.setLocked(domain.isLocked());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}

// -