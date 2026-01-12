package com.monza.app.domain;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String username;
    private String userCode;
    private String password;
    private String role;
    private boolean enabled;
    private LocalDateTime createdAt;

    // constructor
    public User(Long id, String username, String userCode, String password,
                String role, boolean enabled, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.userCode = userCode;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    // constructor2
    public User(String username, String userCode, String password) {
        this.username = username;
        this.userCode = userCode;
        this.password = password;
        this.role = "USER";
        this.enabled = true;
        this.createdAt = LocalDateTime.now();
    }

    // validation
    public void validate() {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username não pode ser vazio");
        }
        if (username.length() > 50) {
            throw new IllegalArgumentException("Username muito longo (máx 50 caracteres)");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public String getUserCode() { return userCode; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isEnabled() { return enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }

}

// -