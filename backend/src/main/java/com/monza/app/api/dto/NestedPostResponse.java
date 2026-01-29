package com.monza.app.api.dto;

import java.time.LocalDateTime;
import java.util.List;

public class NestedPostResponse {
    private Long id;
    private String content;
    private UserResponse author;
    private Long replyToPostId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int upvoteCount;
    private int downvoteCount;
    private Integer currentUserVote;
    private List<NestedPostResponse> replies;
    private int depth;

    public NestedPostResponse() {}

    public NestedPostResponse(Long id, String content, UserResponse author, Long replyToPostId, 
                             LocalDateTime createdAt, LocalDateTime updatedAt, int upvoteCount, 
                             int downvoteCount, Integer currentUserVote, List<NestedPostResponse> replies, int depth) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.replyToPostId = replyToPostId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.upvoteCount = upvoteCount;
        this.downvoteCount = downvoteCount;
        this.currentUserVote = currentUserVote;
        this.replies = replies;
        this.depth = depth;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}

    public UserResponse getAuthor() {return author;}
    public void setAuthor(UserResponse author) {this.author = author;}

    public Long getReplyToPostId() {return replyToPostId;}
    public void setReplyToPostId(Long replyToPostId) {this.replyToPostId = replyToPostId;}

    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}
    public void setUpdatedAt(LocalDateTime updatedAt) {this.updatedAt = updatedAt;}

    public int getUpvoteCount() { return upvoteCount; }
    public void setUpvoteCount(int upvoteCount) {this.upvoteCount = upvoteCount;}

    public int getDownvoteCount() { return downvoteCount; }
    public void setDownvoteCount(int downvoteCount) {this.downvoteCount = downvoteCount;}

    public Integer getCurrentUserVote() { return currentUserVote; }
    public void setCurrentUserVote(Integer currentUserVote) {this.currentUserVote = currentUserVote;}

    public List<NestedPostResponse> getReplies() { return replies; }
    public void setReplies(List<NestedPostResponse> replies) {this.replies = replies;}

    public int getDepth() { return depth; }
    public void setDepth(int depth) {this.depth = depth;}
}
