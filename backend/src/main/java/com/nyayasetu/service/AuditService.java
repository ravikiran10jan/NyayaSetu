package com.nyayasetu.service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.nyayasetu.model.AuditEvent;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class AuditService {

    private final Firestore firestore;
    private static final String COLLECTION = "auditEvents";

    public AuditService(Firestore firestore) {
        this.firestore = firestore;
    }

    public void logEvent(String actorUserId, String actorEmail, String action, 
                        String entityType, String entityId, String details) {
        try {
            AuditEvent event = new AuditEvent();
            DocumentReference docRef = firestore.collection(COLLECTION).document();
            event.setId(docRef.getId());
            event.setActorUserId(actorUserId);
            event.setActorEmail(actorEmail);
            event.setAction(action);
            event.setEntityType(entityType);
            event.setEntityId(entityId);
            event.setDetails(details);
            event.setCreatedAt(System.currentTimeMillis());
            
            docRef.set(event.toMap()).get();
        } catch (ExecutionException | InterruptedException e) {
            // Log error but don't fail the main operation
            System.err.println("Failed to log audit event: " + e.getMessage());
        }
    }
}
