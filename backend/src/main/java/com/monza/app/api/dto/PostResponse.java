package com.monza.app.api.dto;

import java.time.LocalDateTime;

public class PostResponse {
    private Long id;
    private String content;
    private UserResponse author;
    private Long replyToPostId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int upvoteCount;
    private int downvoteCount;
    private Integer currentUserVote; // 1, -1 or null

    // constructor
    public PostResponse(Long id, String content, UserResponse author, Long replyToPostId, LocalDateTime createdAt, LocalDateTime updatedAt, int upvoteCount, int downvoteCount, Integer currentUserVote) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.replyToPostId = replyToPostId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.upvoteCount = upvoteCount;
        this.downvoteCount = downvoteCount;
        this.currentUserVote = currentUserVote;
    }

    // getters
    public Long getId() {return id;}
    public String getContent() {return content;}
    public UserResponse getAuthor() {return author;}
    public Long getReplyToPostId() {return replyToPostId;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
    public int getUpvoteCount() { return upvoteCount; }
    public int getDownvoteCount() { return downvoteCount; }
    public Integer getCurrentUserVote() { return currentUserVote; }
}

// -