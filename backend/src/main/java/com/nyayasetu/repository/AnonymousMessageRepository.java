package com.nyayasetu.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.nyayasetu.model.AnonymousMessage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class AnonymousMessageRepository {

    private final Firestore firestore;
    private static final String COLLECTION = "anonymousMessages";

    public AnonymousMessageRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public AnonymousMessage save(AnonymousMessage message) throws ExecutionException, InterruptedException {
        if (message.getId() == null || message.getId().isEmpty()) {
            DocumentReference docRef = firestore.collection(COLLECTION).document();
            message.setId(docRef.getId());
            ApiFuture<WriteResult> future = docRef.set(message.toMap());
            future.get();
        } else {
            ApiFuture<WriteResult> future = firestore.collection(COLLECTION)
                    .document(message.getId())
                    .set(message.toMap());
            future.get();
        }
        return message;
    }

    public AnonymousMessage findById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection(COLLECTION).document(id).get().get();
        if (snapshot.exists()) {
            return new AnonymousMessage(snapshot.getData());
        }
        return null;
    }

    public List<AnonymousMessage> findByChatId(String chatId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION)
                .whereEqualTo("chatId", chatId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get();
        List<AnonymousMessage> messages = new ArrayList<>();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            messages.add(new AnonymousMessage(document.getData()));
        }
        return messages;
    }

    public AnonymousMessage update(AnonymousMessage message) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION)
                .document(message.getId())
                .set(message.toMap());
        future.get();
        return message;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION).document(id).delete();
        future.get();
    }
}
