package com.nyayasetu.model;

import java.util.Map;

public class LegalMatter {
    private String id;
    private String ownerEmail;
    private String ownerUserId;
    private String lawyerId;
    private String title;
    private String practiceArea;
    private String state;
    private String language;
    private String urgency; // standard, urgent, emergency
    private String opposingParty;
    private String conflictStatus; // pending, clear, conflict
    private String status; // intake, active, closed
    private String cnrNumber;
    private Long createdAt;
    private Long updatedAt;

    public LegalMatter() {}

    public LegalMatter(Map<String, Object> map) {
        this.id = (String) map.get("id");
        this.ownerEmail = (String) map.get("ownerEmail");
        this.ownerUserId = (String) map.get("ownerUserId");
        this.lawyerId = (String) map.get("lawyerId");
        this.title = (String) map.get("title");
        this.practiceArea = (String) map.get("practiceArea");
        this.state = (String) map.get("state");
        this.language = (String) map.get("language");
        this.urgency = (String) map.get("urgency");
        this.opposingParty = (String) map.get("opposingParty");
        this.conflictStatus = (String) map.get("conflictStatus");
        this.status = (String) map.get("status");
        this.cnrNumber = (String) map.get("cnrNumber");
        this.createdAt = map.get("createdAt") instanceof Number ? ((Number) map.get("createdAt")).longValue() : null;
        this.updatedAt = map.get("updatedAt") instanceof Number ? ((Number) map.get("updatedAt")).longValue() : null;
    }

    public Map<String, Object> toMap() {
        return Map.ofEntries(
            Map.entry("id", id != null ? id : ""),
            Map.entry("ownerEmail", ownerEmail),
            Map.entry("ownerUserId", ownerUserId),
            Map.entry("lawyerId", lawyerId != null ? lawyerId : ""),
            Map.entry("title", title),
            Map.entry("practiceArea", practiceArea != null ? practiceArea : ""),
            Map.entry("state", state != null ? state : ""),
            Map.entry("language", language != null ? language : ""),
            Map.entry("urgency", urgency != null ? urgency : "standard"),
            Map.entry("opposingParty", opposingParty != null ? opposingParty : ""),
            Map.entry("conflictStatus", conflictStatus != null ? conflictStatus : "pending"),
            Map.entry("status", status != null ? status : "intake"),
            Map.entry("cnrNumber", cnrNumber != null ? cnrNumber : ""),
            Map.entry("createdAt", createdAt),
            Map.entry("updatedAt", updatedAt)
        );
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
    public String getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(String ownerUserId) { this.ownerUserId = ownerUserId; }
    public String getLawyerId() { return lawyerId; }
    public void setLawyerId(String lawyerId) { this.lawyerId = lawyerId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPracticeArea() { return practiceArea; }
    public void setPracticeArea(String practiceArea) { this.practiceArea = practiceArea; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    public String getOpposingParty() { return opposingParty; }
    public void setOpposingParty(String opposingParty) { this.opposingParty = opposingParty; }
    public String getConflictStatus() { return conflictStatus; }
    public void setConflictStatus(String conflictStatus) { this.conflictStatus = conflictStatus; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCnrNumber() { return cnrNumber; }
    public void setCnrNumber(String cnrNumber) { this.cnrNumber = cnrNumber; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
