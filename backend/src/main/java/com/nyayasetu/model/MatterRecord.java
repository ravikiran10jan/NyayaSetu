package com.nyayasetu.model;

import java.util.Map;

public class MatterRecord {
    private String id;
    private String matterId;
    private String author;
    private String kind; // note, task, advice, document_request, timeline
    private String title;
    private String body;
    private Long dueAt;
    private Long completedAt;
    private Long createdAt;

    public MatterRecord() {}

    public MatterRecord(Map<String, Object> map) {
        this.id = (String) map.get("id");
        this.matterId = (String) map.get("matterId");
        this.author = (String) map.get("author");
        this.kind = (String) map.get("kind");
        this.title = (String) map.get("title");
        this.body = (String) map.get("body");
        this.dueAt = map.get("dueAt") instanceof Number ? ((Number) map.get("dueAt")).longValue() : null;
        this.completedAt = map.get("completedAt") instanceof Number ? ((Number) map.get("completedAt")).longValue() : null;
        this.createdAt = map.get("createdAt") instanceof Number ? ((Number) map.get("createdAt")).longValue() : null;
    }

    public Map<String, Object> toMap() {
        return Map.ofEntries(
            Map.entry("id", id != null ? id : ""),
            Map.entry("matterId", matterId),
            Map.entry("author", author),
            Map.entry("kind", kind),
            Map.entry("title", title != null ? title : ""),
            Map.entry("body", body != null ? body : ""),
            Map.entry("dueAt", dueAt != null ? dueAt : 0L),
            Map.entry("completedAt", completedAt != null ? completedAt : 0L),
            Map.entry("createdAt", createdAt)
        );
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMatterId() { return matterId; }
    public void setMatterId(String matterId) { this.matterId = matterId; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Long getDueAt() { return dueAt; }
    public void setDueAt(Long dueAt) { this.dueAt = dueAt; }
    public Long getCompletedAt() { return completedAt; }
    public void setCompletedAt(Long completedAt) { this.completedAt = completedAt; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
}
