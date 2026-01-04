package com.monza.app.api.dto;

import java.time.LocalDateTime;

public class UserResponse {
    private Long id;
    private String username;
    private String userCode;
    private LocalDateTime createdAt;

    // constructor
    public UserResponse(Long id, String username, String userCode, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.userCode = userCode;
        this.createdAt = createdAt;
    }

    // getters
    public Long getId() {return id;}
    public String getUsername() {return username;}
    public String getUserCode() {return userCode;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}

// -