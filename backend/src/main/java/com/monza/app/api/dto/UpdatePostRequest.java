package com.monza.app.api.dto;

public class UpdatePostRequest {
    private String content;

    public UpdatePostRequest() {}

    public UpdatePostRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
