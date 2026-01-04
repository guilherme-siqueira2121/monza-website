package com.monza.app.api.dto;

public class CreateThreadRequest {
    private Long boardId;
    private Long userId;
    private String title;
    private String content;

    // constructor
    public CreateThreadRequest() {}

    // getters and setters
    public Long getBoardId() {return boardId;}
    public void setBoardId(Long boardId) {this.boardId = boardId;}
    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}
    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}
}

// -