package com.monza.app.domain;

import java.time.LocalDateTime;

public class Board {
    private Long id;
    private String name; // ex: /tech/
    private String title; // ex: "Technology"
    private String description;
    private LocalDateTime createdAt;

    public Board(Long id, String name, String title, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}
    public String getTitle() {return title;}
    public String getDescription() {return description;}
    public LocalDateTime getCreatedAt() {return createdAt;}

}

// -