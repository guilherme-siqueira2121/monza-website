package com.monza.app.api.dto;

import java.time.LocalDateTime;

public class PostResponse {
    private Long id;
    private String content;
    private UserResponse author;
    private Long replyToPostId;
    private LocalDateTime createAt;

    // constructor
    public PostResponse(Long id, String content, UserResponse author, Long replyToPostId, LocalDateTime createAt) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.replyToPostId = replyToPostId;
        this.createAt = createAt;
    }

    // getters
    public Long getId() {return id;}
    public String getContent() {return content;}
    public UserResponse getAuthor() {return author;}
    public Long getReplyToPostId() {return replyToPostId;}
    public LocalDateTime getCreateAt() {return createAt;}
}

// -