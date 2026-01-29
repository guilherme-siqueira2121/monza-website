package com.monza.app.api.dto;

public class CreatePostRequest {
    private Long threadId;
    private Long userId;
    private String content;
    private Long replyToPostId;

    public CreatePostRequest() {}

    public Long getThreadId() {return threadId;}
    public void setThreadId(Long threadId) {this.threadId = threadId;}
    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}
    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}
    public Long getReplyToPostId() {return replyToPostId;}
    public void setReplyToPostId(Long replyToPostId) {this.replyToPostId = replyToPostId;}
}

// -