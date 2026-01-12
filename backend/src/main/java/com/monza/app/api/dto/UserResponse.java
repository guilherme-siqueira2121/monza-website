package com.monza.app.api.dto;

import java.time.LocalDateTime;

public class UserResponse {
    private Long id;
    private String username;
    private String userCode;
    private String role;
    private LocalDateTime createdAt;

    // constructor
    public UserResponse(Long id, String username, String userCode,
                        String role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.userCode = userCode;
        this.role = role;
        this.createdAt = createdAt;
    }

    // getters
    public Long getId() {return id;}
    public String getUsername() {return username;}
    public String getUserCode() {return userCode;}
    public String getRole() {return role;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}

// -