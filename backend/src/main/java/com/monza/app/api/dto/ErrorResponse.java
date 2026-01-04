package com.monza.app.api.dto;

public class ErrorResponse {
    private String message;

    // constructor
    public ErrorResponse(String message) {
        this.message = message;
    }

    // getters and setters
    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}
}

// -