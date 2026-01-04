package com.monza.app.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "user_code", nullable = false, unique = true, length = 10)
    private String userCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // constructor
    public UserEntity() {}

    public UserEntity(String username, String userCode, LocalDateTime createdAt) {
        this.username = username;
        this.userCode = userCode;
        this.createdAt = createdAt;
    }

    // getters and setters
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getUserCode() {return userCode;}
    public void setUserCode(String userCode) {this.userCode = userCode;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
}

// -