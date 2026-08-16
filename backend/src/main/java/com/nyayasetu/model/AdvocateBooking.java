package com.nyayasetu.model;

import java.util.Map;

public class AdvocateBooking {
    private String id;
    private String clientEmail;
    private String clientUserId;
    private String clientAlias;
    private String lawyerId;
    private String practiceArea;
    private String mode; // chat, call
    private Long scheduledStartsAt;
    private Long scheduledEndsAt;
    private String status; // requested, accepted, cancelled, completed
    private String clientNote;
    private Long acceptedAt;
    private Long completedAt;
    private Long version;
    private Long createdAt;
    private Long updatedAt;

    public AdvocateBooking() {}

    public AdvocateBooking(Map<String, Object> map) {
        this.id = (String) map.get("id");
        this.clientEmail = (String) map.get("clientEmail");
        this.clientUserId = (String) map.get("clientUserId");
        this.clientAlias = (String) map.get("clientAlias");
        this.lawyerId = (String) map.get("lawyerId");
        this.practiceArea = (String) map.get("practiceArea");
        this.mode = (String) map.get("mode");
        this.scheduledStartsAt = map.get("scheduledStartsAt") instanceof Number ? ((Number) map.get("scheduledStartsAt")).longValue() : null;
        this.scheduledEndsAt = map.get("scheduledEndsAt") instanceof Number ? ((Number) map.get("scheduledEndsAt")).longValue() : null;
        this.status = (String) map.get("status");
        this.clientNote = (String) map.get("clientNote");
        this.acceptedAt = map.get("acceptedAt") instanceof Number ? ((Number) map.get("acceptedAt")).longValue() : null;
        this.completedAt = map.get("completedAt") instanceof Number ? ((Number) map.get("completedAt")).longValue() : null;
        this.version = map.get("version") instanceof Number ? ((Number) map.get("version")).longValue() : null;
        this.createdAt = map.get("createdAt") instanceof Number ? ((Number) map.get("createdAt")).longValue() : null;
        this.updatedAt = map.get("updatedAt") instanceof Number ? ((Number) map.get("updatedAt")).longValue() : null;
    }

    public Map<String, Object> toMap() {
        return Map.ofEntries(
            Map.entry("id", id != null ? id : ""),
            Map.entry("clientEmail", clientEmail),
            Map.entry("clientUserId", clientUserId),
            Map.entry("clientAlias", clientAlias != null ? clientAlias : ""),
            Map.entry("lawyerId", lawyerId),
            Map.entry("practiceArea", practiceArea),
            Map.entry("mode", mode),
            Map.entry("scheduledStartsAt", scheduledStartsAt),
            Map.entry("scheduledEndsAt", scheduledEndsAt),
            Map.entry("status", status),
            Map.entry("clientNote", clientNote != null ? clientNote : ""),
            Map.entry("acceptedAt", acceptedAt != null ? acceptedAt : 0L),
            Map.entry("completedAt", completedAt != null ? completedAt : 0L),
            Map.entry("version", version != null ? version : 1L),
            Map.entry("createdAt", createdAt),
            Map.entry("updatedAt", updatedAt)
        );
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }
    public String getClientUserId() { return clientUserId; }
    public void setClientUserId(String clientUserId) { this.clientUserId = clientUserId; }
    public String getClientAlias() { return clientAlias; }
    public void setClientAlias(String clientAlias) { this.clientAlias = clientAlias; }
    public String getLawyerId() { return lawyerId; }
    public void setLawyerId(String lawyerId) { this.lawyerId = lawyerId; }
    public String getPracticeArea() { return practiceArea; }
    public void setPracticeArea(String practiceArea) { this.practiceArea = practiceArea; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public Long getScheduledStartsAt() { return scheduledStartsAt; }
    public void setScheduledStartsAt(Long scheduledStartsAt) { this.scheduledStartsAt = scheduledStartsAt; }
    public Long getScheduledEndsAt() { return scheduledEndsAt; }
    public void setScheduledEndsAt(Long scheduledEndsAt) { this.scheduledEndsAt = scheduledEndsAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getClientNote() { return clientNote; }
    public void setClientNote(String clientNote) { this.clientNote = clientNote; }
    public Long getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(Long acceptedAt) { this.acceptedAt = acceptedAt; }
    public Long getCompletedAt() { return completedAt; }
    public void setCompletedAt(Long completedAt) { this.completedAt = completedAt; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
