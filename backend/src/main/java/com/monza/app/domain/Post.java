package com.monza.app.domain;

import java.time.LocalDateTime;

public class Post {
    private Long id;
    private Long threadId;
    private Long userId;
    private String content;
    private Long replyToPostId; // null if not for reply
    private LocalDateTime createdAt;

    // constructor
    public Post(Long id, Long threadId, Long userId, String content,
                Long replyToPostId, LocalDateTime createdAt) {
        this.id = id;
        this.threadId = threadId;
        this.userId = userId;
        this.content = content;
        this.replyToPostId = replyToPostId;
        this.createdAt = createdAt;
    }

    // constructor2
    public Post(Long threadId, Long userId, String content, Long replyToPostId) {
        this.threadId = threadId;
        this.userId = userId;
        this.content = content;
        this.replyToPostId = replyToPostId;
        this.createdAt = LocalDateTime.now();
    }

    // domain validation
    public void validate() {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Conteúdo não pode ser vazio");
        }
    }

    //getters
    public Long getId() {return id;}
    public Long getThreadId() {return threadId;}
    public Long getUserId() {return userId;}
    public String getContent() {return content;}
    public Long getReplyToPostId() {return replyToPostId;}
    public LocalDateTime getCreatedAt() {return createdAt;}

    // setters
    public void setId(Long id) {this.id = id;}
}

// -