package com.monza.app.api.dto;

public class VoteRequest {
    private int value;

    public VoteRequest() {}

    public VoteRequest(int value) { this.value = value; }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
}
