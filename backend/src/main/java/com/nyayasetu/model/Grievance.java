package com.nyayasetu.model;

import java.util.Map;

public class Grievance {
    private String id;
    private String reporterEmail;
    private String reporterUserId;
    private String matterId;
    private String category;
    private String details;
    private String status; // open, reviewing, resolved
    private Long createdAt;
    private Long updatedAt;

    public Grievance() {}

    public Grievance(Map<String, Object> map) {
        this.id = (String) map.get("id");
        this.reporterEmail = (String) map.get("reporterEmail");
        this.reporterUserId = (String) map.get("reporterUserId");
        this.matterId = (String) map.get("matterId");
        this.category = (String) map.get("category");
        this.details = (String) map.get("details");
        this.status = (String) map.get("status");
        this.createdAt = map.get("createdAt") instanceof Number ? ((Number) map.get("createdAt")).longValue() : null;
        this.updatedAt = map.get("updatedAt") instanceof Number ? ((Number) map.get("updatedAt")).longValue() : null;
    }

    public Map<String, Object> toMap() {
        return Map.ofEntries(
            Map.entry("id", id != null ? id : ""),
            Map.entry("reporterEmail", reporterEmail),
            Map.entry("reporterUserId", reporterUserId),
            Map.entry("matterId", matterId != null ? matterId : ""),
            Map.entry("category", category),
            Map.entry("details", details),
            Map.entry("status", status != null ? status : "open"),
            Map.entry("createdAt", createdAt),
            Map.entry("updatedAt", updatedAt)
        );
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getReporterEmail() { return reporterEmail; }
    public void setReporterEmail(String reporterEmail) { this.reporterEmail = reporterEmail; }
    public String getReporterUserId() { return reporterUserId; }
    public void setReporterUserId(String reporterUserId) { this.reporterUserId = reporterUserId; }
    public String getMatterId() { return matterId; }
    public void setMatterId(String matterId) { this.matterId = matterId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
