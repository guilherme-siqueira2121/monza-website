package com.monza.app.api.dto;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String username;
    private String userCode;
    private String role;

    public AuthResponse(String accessToken, String refreshToken, Long userId,
                        String username, String userCode, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.username = username;
        this.userCode = userCode;
        this.role = role;
    }

    // getters
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getUserCode() { return userCode; }
    public String getRole() { return role; }
}

// -