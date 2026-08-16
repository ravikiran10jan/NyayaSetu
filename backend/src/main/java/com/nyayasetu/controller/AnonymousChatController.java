package com.nyayasetu.controller;

import com.nyayasetu.dto.AnonymousChatRequest;
import com.nyayasetu.dto.AnonymousChatResponse;
import com.nyayasetu.dto.ApiResponse;
import com.nyayasetu.model.AnonymousChat;
import com.nyayasetu.model.AnonymousMessage;
import com.nyayasetu.service.AnonymousChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/anonymous")
public class AnonymousChatController {

    private final AnonymousChatService chatService;

    public AnonymousChatController(AnonymousChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chats")
    public ResponseEntity<ApiResponse<AnonymousChatResponse>> createChat(
            @RequestBody AnonymousChatRequest request) throws ExecutionException, InterruptedException {
        
        try {
            AnonymousChat chat = chatService.createChat(request);
            
            // Generate token and save hash
            String token = chatService.generateToken();
            String tokenHash = chatService.hashToken(token);
            chatService.saveTokenHash(chat.getId(), tokenHash);
            
            AnonymousChatResponse response = new AnonymousChatResponse(chat, token);
            return ResponseEntity.status(201).body(
                ApiResponse.success("Chat created successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/chats/{id}/messages")
    public ResponseEntity<ApiResponse<List<AnonymousMessage>>> getMessages(
            @PathVariable String id,
            @RequestParam String token) throws ExecutionException, InterruptedException {
        
        boolean isValid = chatService.validateToken(id, token);
        if (!isValid) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid or expired token"));
        }
        
        List<AnonymousMessage> messages = chatService.getMessages(id);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @PostMapping("/chats/{id}/messages")
    public ResponseEntity<ApiResponse<AnonymousMessage>> sendMessage(
            @PathVariable String id,
            @RequestParam String token,
            @RequestBody Map<String, String> body) throws ExecutionException, InterruptedException {
        
        boolean isValid = chatService.validateToken(id, token);
        if (!isValid) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid or expired token"));
        }
        
        try {
            String messageBody = body.get("body");
            if (messageBody == null || messageBody.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Message body is required"));
            }
            
            AnonymousMessage message = chatService.sendMessage(id, "anonymous", messageBody);
            return ResponseEntity.status(201).body(
                ApiResponse.success("Message sent successfully", message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(410).body(ApiResponse.error(e.getMessage()));
        }
    }
}
