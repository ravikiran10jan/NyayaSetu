package com.nyayasetu.dto;

import com.nyayasetu.model.AnonymousChat;

public class AnonymousChatResponse {
    private String chatId;
    private String token;
    private Long expiresAt;

    public AnonymousChatResponse() {}

    public AnonymousChatResponse(AnonymousChat chat, String token) {
        this.chatId = chat.getId();
        this.token = token;
        this.expiresAt = chat.getExpiresAt();
    }

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Long expiresAt) { this.expiresAt = expiresAt; }
}
