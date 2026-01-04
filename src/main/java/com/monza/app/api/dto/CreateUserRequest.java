package com.monza.app.api.dto;

public class CreateUserRequest {
    private String username;

    // constructor
    public CreateUserRequest() {}

    // constructor of parameter
    public CreateUserRequest(String username) {
        this.username = username;
    }

    // getters e setters
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
}

// -