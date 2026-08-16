package com.nyayasetu.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.nyayasetu.model.Grievance;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class GrievanceRepository {

    private final Firestore firestore;
    private static final String COLLECTION = "grievances";

    public GrievanceRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public Grievance save(Grievance grievance) throws ExecutionException, InterruptedException {
        if (grievance.getId() == null || grievance.getId().isEmpty()) {
            DocumentReference docRef = firestore.collection(COLLECTION).document();
            grievance.setId(docRef.getId());
            ApiFuture<WriteResult> future = docRef.set(grievance.toMap());
            future.get();
        } else {
            ApiFuture<WriteResult> future = firestore.collection(COLLECTION)
                    .document(grievance.getId())
                    .set(grievance.toMap());
            future.get();
        }
        return grievance;
    }

    public Grievance findById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection(COLLECTION).document(id).get().get();
        if (snapshot.exists()) {
            return new Grievance(snapshot.getData());
        }
        return null;
    }

    public List<Grievance> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get();
        List<Grievance> grievances = new ArrayList<>();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            grievances.add(new Grievance(document.getData()));
        }
        return grievances;
    }

    public List<Grievance> findByReporterUserId(String reporterUserId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION)
                .whereEqualTo("reporterUserId", reporterUserId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get();
        List<Grievance> grievances = new ArrayList<>();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            grievances.add(new Grievance(document.getData()));
        }
        return grievances;
    }

    public Grievance update(Grievance grievance) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION)
                .document(grievance.getId())
                .set(grievance.toMap());
        future.get();
        return grievance;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION).document(id).delete();
        future.get();
    }
}
