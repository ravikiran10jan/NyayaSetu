package com.nyayasetu.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.nyayasetu.model.AnonymousChat;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class AnonymousChatRepository {

    private final Firestore firestore;
    private static final String COLLECTION = "anonymousChats";

    public AnonymousChatRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public AnonymousChat save(AnonymousChat chat) throws ExecutionException, InterruptedException {
        if (chat.getId() == null || chat.getId().isEmpty()) {
            DocumentReference docRef = firestore.collection(COLLECTION).document();
            chat.setId(docRef.getId());
            ApiFuture<WriteResult> future = docRef.set(chat.toMap());
            future.get();
        } else {
            ApiFuture<WriteResult> future = firestore.collection(COLLECTION)
                    .document(chat.getId())
                    .set(chat.toMap());
            future.get();
        }
        return chat;
    }

    public AnonymousChat findById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection(COLLECTION).document(id).get().get();
        if (snapshot.exists()) {
            return new AnonymousChat(snapshot.getData());
        }
        return null;
    }

    public List<AnonymousChat> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION).get();
        List<AnonymousChat> chats = new ArrayList<>();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            chats.add(new AnonymousChat(document.getData()));
        }
        return chats;
    }

    public AnonymousChat update(AnonymousChat chat) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION)
                .document(chat.getId())
                .set(chat.toMap());
        future.get();
        return chat;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION).document(id).delete();
        future.get();
    }
}
