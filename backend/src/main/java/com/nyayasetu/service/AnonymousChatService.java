package com.nyayasetu.service;

import com.nyayasetu.dto.AnonymousChatRequest;
import com.nyayasetu.model.AnonymousChat;
import com.nyayasetu.model.AnonymousMessage;
import com.nyayasetu.repository.AnonymousChatRepository;
import com.nyayasetu.repository.AnonymousMessageRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class AnonymousChatService {

    private final AnonymousChatRepository chatRepository;
    private final AnonymousMessageRepository messageRepository;

    public AnonymousChatService(AnonymousChatRepository chatRepository, 
                               AnonymousMessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }

    public AnonymousChat createChat(AnonymousChatRequest request) throws ExecutionException, InterruptedException {
        if (!Boolean.TRUE.equals(request.getConsent())) {
            throw new IllegalArgumentException("Consent is required to start anonymous chat");
        }

        AnonymousChat chat = new AnonymousChat();
        chat.setAlias(request.getAlias());
        chat.setPhoneMask(request.getPhoneMask());
        chat.setPhoneHash(request.getPhoneHash());
        chat.setLawyerId(request.getLawyerId());
        chat.setStatus("open");
        
        long now = System.currentTimeMillis();
        chat.setCreatedAt(now);
        chat.setLastActivityAt(now);
        
        // Set expiry to 24 hours from now
        chat.setExpiresAt(now + (24 * 60 * 60 * 1000));

        return chatRepository.save(chat);
    }

    public String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return bytesToHex(bytes);
    }

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public void saveTokenHash(String chatId, String tokenHash) throws ExecutionException, InterruptedException {
        AnonymousChat chat = chatRepository.findById(chatId);
        if (chat != null) {
            chat.setTokenHash(tokenHash);
            chatRepository.update(chat);
        }
    }

    public AnonymousChat getChatById(String id) throws ExecutionException, InterruptedException {
        return chatRepository.findById(id);
    }

    public boolean validateToken(String chatId, String token) throws ExecutionException, InterruptedException {
        AnonymousChat chat = chatRepository.findById(chatId);
        if (chat == null) {
            return false;
        }

        // Check if chat is expired
        if (chat.getExpiresAt() != null && chat.getExpiresAt() < System.currentTimeMillis()) {
            return false;
        }

        // Check if chat is closed
        if ("closed".equals(chat.getStatus())) {
            return false;
        }

        // Validate token hash
        String tokenHash = hashToken(token);
        return tokenHash.equals(chat.getTokenHash());
    }

    public AnonymousMessage sendMessage(String chatId, String sender, String body) 
            throws ExecutionException, InterruptedException {
        
        AnonymousChat chat = chatRepository.findById(chatId);
        if (chat == null) {
            throw new IllegalArgumentException("Chat not found");
        }

        // Check if chat is expired
        if (chat.getExpiresAt() != null && chat.getExpiresAt() < System.currentTimeMillis()) {
            throw new IllegalStateException("Chat has expired");
        }

        // Check if chat is closed
        if ("closed".equals(chat.getStatus())) {
            throw new IllegalStateException("Chat is closed");
        }

        AnonymousMessage message = new AnonymousMessage();
        message.setChatId(chatId);
        message.setSender(sender);
        message.setBody(body);
        message.setCreatedAt(System.currentTimeMillis());
        message.setExpiresAt(chat.getExpiresAt());

        message = messageRepository.save(message);

        // Update last activity
        chat.setLastActivityAt(System.currentTimeMillis());
        chatRepository.update(chat);

        return message;
    }

    public List<AnonymousMessage> getMessages(String chatId) throws ExecutionException, InterruptedException {
        return messageRepository.findByChatId(chatId);
    }
}
