package com.monza.app.domain;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String username;
    private String userCode; // código único (ex: URS001)
    private LocalDateTime createdAt;

    // constructor
    public User(Long id, String username, String userCode, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.userCode = userCode;
        this.createdAt = createdAt;
    }

    // constructor2
    public User(String username, String userCode) {
        this.username = username;
        this.userCode = userCode;
        this.createdAt = LocalDateTime.now();
    }

    // domain validation
    public void validate() {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username não pode ser vazio");
        }
        if (username.length() > 50) {
            throw new IllegalArgumentException("Username muito longo (máx 50 caracteres)");
        }
        if (userCode == null || userCode.trim().isEmpty()) {
            throw new IllegalArgumentException("UserCode não pode ser vazio");
        }
    }

    // getters and setters
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getUsername() {return username;}
    public String getUserCode() {return userCode;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}

// -