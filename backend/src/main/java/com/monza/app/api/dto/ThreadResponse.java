package com.monza.app.api.dto;

import java.time.LocalDateTime;

public class ThreadResponse {
    private Long id;
    private String title;
    private String content;
    private UserResponse author;
    private long postCount;
    private boolean isPinned;
    private boolean isLocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ThreadResponse(Long id, String title, String content, UserResponse author,
                          long postCount, boolean isPinned, boolean isLocked,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.postCount = postCount;
        this.isPinned = isPinned;
        this.isLocked = isLocked;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {return id;}
    public String getTitle() {return title;}
    public String getContent() {return content;}
    public UserResponse getAuthor() {return author;}
    public long getPostCount() {return postCount;}
    public boolean isPinned() {return isPinned;}
    public boolean isLocked() {return isLocked;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
}

// -