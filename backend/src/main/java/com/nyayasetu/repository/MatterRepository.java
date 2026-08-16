package com.nyayasetu.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.nyayasetu.model.LegalMatter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class MatterRepository {

    private final Firestore firestore;
    private static final String COLLECTION = "legalMatters";

    public MatterRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public LegalMatter save(LegalMatter matter) throws ExecutionException, InterruptedException {
        if (matter.getId() == null || matter.getId().isEmpty()) {
            DocumentReference docRef = firestore.collection(COLLECTION).document();
            matter.setId(docRef.getId());
            ApiFuture<WriteResult> future = docRef.set(matter.toMap());
            future.get();
        } else {
            ApiFuture<WriteResult> future = firestore.collection(COLLECTION)
                    .document(matter.getId())
                    .set(matter.toMap());
            future.get();
        }
        return matter;
    }

    public LegalMatter findById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection(COLLECTION).document(id).get().get();
        if (snapshot.exists()) {
            return new LegalMatter(snapshot.getData());
        }
        return null;
    }

    public List<LegalMatter> findByOwnerUserId(String ownerUserId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION)
                .whereEqualTo("ownerUserId", ownerUserId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get();
        List<LegalMatter> matters = new ArrayList<>();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            matters.add(new LegalMatter(document.getData()));
        }
        return matters;
    }

    public List<LegalMatter> findByLawyerId(String lawyerId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION)
                .whereEqualTo("lawyerId", lawyerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get();
        List<LegalMatter> matters = new ArrayList<>();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            matters.add(new LegalMatter(document.getData()));
        }
        return matters;
    }

    public LegalMatter update(LegalMatter matter) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION)
                .document(matter.getId())
                .set(matter.toMap());
        future.get();
        return matter;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION).document(id).delete();
        future.get();
    }
}
