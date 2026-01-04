package com.monza.app.persistence.mapper;

import com.monza.app.domain.*;
import com.monza.app.persistence.entity.*;
import org.springframework.stereotype.Component;

@Component
public class BoardMapper {

    // converts entity to domain
    public Board toDomain(BoardEntity entity) {
        if (entity == null) return null;
        return new Board(
                entity.getId(),
                entity.getName(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCreatedAt()
        );
    }
}

// -