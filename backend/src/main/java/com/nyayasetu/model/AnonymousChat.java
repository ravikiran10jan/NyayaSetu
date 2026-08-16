package com.nyayasetu.model;

import java.util.Map;

public class AnonymousChat {
    private String id;
    private String tokenHash;
    private String alias;
    private String phoneMask;
    private String phoneHash;
    private String lawyerId;
    private String status; // open, closed, expired
    private Long createdAt;
    private Long expiresAt;
    private Long lastActivityAt;

    public AnonymousChat() {}

    public AnonymousChat(Map<String, Object> map) {
        this.id = (String) map.get("id");
        this.tokenHash = (String) map.get("tokenHash");
        this.alias = (String) map.get("alias");
        this.phoneMask = (String) map.get("phoneMask");
        this.phoneHash = (String) map.get("phoneHash");
        this.lawyerId = (String) map.get("lawyerId");
        this.status = (String) map.get("status");
        this.createdAt = map.get("createdAt") instanceof Number ? ((Number) map.get("createdAt")).longValue() : null;
        this.expiresAt = map.get("expiresAt") instanceof Number ? ((Number) map.get("expiresAt")).longValue() : null;
        this.lastActivityAt = map.get("lastActivityAt") instanceof Number ? ((Number) map.get("lastActivityAt")).longValue() : null;
    }

    public Map<String, Object> toMap() {
        return Map.ofEntries(
            Map.entry("id", id != null ? id : ""),
            Map.entry("tokenHash", tokenHash),
            Map.entry("alias", alias),
            Map.entry("phoneMask", phoneMask),
            Map.entry("phoneHash", phoneHash),
            Map.entry("lawyerId", lawyerId != null ? lawyerId : ""),
            Map.entry("status", status),
            Map.entry("createdAt", createdAt),
            Map.entry("expiresAt", expiresAt),
            Map.entry("lastActivityAt", lastActivityAt)
        );
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public String getPhoneMask() { return phoneMask; }
    public void setPhoneMask(String phoneMask) { this.phoneMask = phoneMask; }
    public String getPhoneHash() { return phoneHash; }
    public void setPhoneHash(String phoneHash) { this.phoneHash = phoneHash; }
    public String getLawyerId() { return lawyerId; }
    public void setLawyerId(String lawyerId) { this.lawyerId = lawyerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Long expiresAt) { this.expiresAt = expiresAt; }
    public Long getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(Long lastActivityAt) { this.lastActivityAt = lastActivityAt; }
}
