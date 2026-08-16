package com.nyayasetu.model;

import java.util.Map;

public class AuditEvent {
    private String id;
    private String actorUserId;
    private String actorEmail;
    private String action;
    private String entityType;
    private String entityId;
    private String details;
    private Long createdAt;

    public AuditEvent() {}

    public AuditEvent(Map<String, Object> map) {
        this.id = (String) map.get("id");
        this.actorUserId = (String) map.get("actorUserId");
        this.actorEmail = (String) map.get("actorEmail");
        this.action = (String) map.get("action");
        this.entityType = (String) map.get("entityType");
        this.entityId = (String) map.get("entityId");
        this.details = (String) map.get("details");
        this.createdAt = map.get("createdAt") instanceof Number ? ((Number) map.get("createdAt")).longValue() : null;
    }

    public Map<String, Object> toMap() {
        return Map.ofEntries(
            Map.entry("id", id != null ? id : ""),
            Map.entry("actorUserId", actorUserId),
            Map.entry("actorEmail", actorEmail),
            Map.entry("action", action),
            Map.entry("entityType", entityType != null ? entityType : ""),
            Map.entry("entityId", entityId != null ? entityId : ""),
            Map.entry("details", details != null ? details : ""),
            Map.entry("createdAt", createdAt)
        );
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getActorUserId() { return actorUserId; }
    public void setActorUserId(String actorUserId) { this.actorUserId = actorUserId; }
    public String getActorEmail() { return actorEmail; }
    public void setActorEmail(String actorEmail) { this.actorEmail = actorEmail; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
}
