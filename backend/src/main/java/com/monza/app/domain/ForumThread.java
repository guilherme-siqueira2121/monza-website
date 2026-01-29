package com.monza.app.domain;

import java.time.LocalDateTime;

public class ForumThread {
    private Long id;
    private Long boardId;
    private Long userId;
    private String title;
    private String content;
    private boolean isPinned;
    private boolean isLocked;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public ForumThread(Long id, Long boardId, Long userId, String title, String content,
                       boolean isPinned, boolean isLocked, LocalDateTime createdAt, LocalDateTime updateAt) {
        this.id = id;
        this.boardId = boardId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
        this.isLocked = isLocked;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }

    public ForumThread(Long boardId, Long userId, String title, String content) {
        this.boardId = boardId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.isPinned = false;
        this.isLocked = false;
        this.createdAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    public void validate() {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Título não pode ser vazio");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Conteúdo não pode ser vazio");
        }
        if (title.length() > 300) {
            throw new IllegalArgumentException("Título muito longo");
        }
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public Long getBoardId() {return boardId;}
    public Long getUserId() {return userId;}
    public String getTitle() {return title;}
    public String getContent() {return content;}
    public boolean isPinned() {return isPinned;}
    public boolean isLocked() {return isLocked;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updateAt;}
}

// -