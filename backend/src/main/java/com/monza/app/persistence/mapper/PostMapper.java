package com.monza.app.persistence.mapper;

import com.monza.app.domain.*;
import com.monza.app.persistence.entity.*;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public Post toDomain(PostEntity entity) {
        if (entity == null) return null;
        return new Post(
                entity.getId(),
                entity.getThreadId(),
                entity.getUserId(),
                entity.getContent(),
                entity.getReplyToPostId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public PostEntity toEntity(Post domain) {
        if (domain == null) return null;
        PostEntity entity = new PostEntity();
        entity.setId(domain.getId());
        entity.setThreadId(domain.getThreadId());
        entity.setUserId(domain.getUserId());
        entity.setContent(domain.getContent());
        entity.setReplyToPostId(domain.getReplyToPostId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}

// -