package com.nyayasetu.model;

import java.util.Map;

public class AnonymousMessage {
    private String id;
    private String chatId;
    private String sender; // anonymous, advocate, system
    private String body;
    private Long createdAt;
    private Long expiresAt;

    public AnonymousMessage() {}

    public AnonymousMessage(Map<String, Object> map) {
        this.id = (String) map.get("id");
        this.chatId = (String) map.get("chatId");
        this.sender = (String) map.get("sender");
        this.body = (String) map.get("body");
        this.createdAt = map.get("createdAt") instanceof Number ? ((Number) map.get("createdAt")).longValue() : null;
        this.expiresAt = map.get("expiresAt") instanceof Number ? ((Number) map.get("expiresAt")).longValue() : null;
    }

    public Map<String, Object> toMap() {
        return Map.ofEntries(
            Map.entry("id", id != null ? id : ""),
            Map.entry("chatId", chatId),
            Map.entry("sender", sender),
            Map.entry("body", body),
            Map.entry("createdAt", createdAt),
            Map.entry("expiresAt", expiresAt)
        );
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Long expiresAt) { this.expiresAt = expiresAt; }
}
