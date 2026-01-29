package com.monza.app.api.dto;

public class BoardResponse {
    private Long id;
    private String name;
    private String title;
    private String description;
    private long threadCount;

    public BoardResponse(Long id, String name, String title, String description, long threadCount) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.description = description;
        this.threadCount = threadCount;
    }

    public Long getId() {return id;}
    public String getName() {return name;}
    public String getTitle() {return title;}
    public String getDescription() {return description;}
    public long getThreadCount() {return threadCount;}
}

// -