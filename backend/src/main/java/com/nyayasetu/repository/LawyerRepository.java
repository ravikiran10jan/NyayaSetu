package com.nyayasetu.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.nyayasetu.model.Lawyer;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class LawyerRepository {

    private final Firestore firestore;
    private static final String COLLECTION = "lawyers";

    public LawyerRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public Lawyer save(Lawyer lawyer) throws ExecutionException, InterruptedException {
        if (lawyer.getId() == null || lawyer.getId().isEmpty()) {
            DocumentReference docRef = firestore.collection(COLLECTION).document();
            lawyer.setId(docRef.getId());
            ApiFuture<WriteResult> future = docRef.set(lawyer.toMap());
            future.get();
        } else {
            ApiFuture<WriteResult> future = firestore.collection(COLLECTION)
                    .document(lawyer.getId())
                    .set(lawyer.toMap());
            future.get();
        }
        return lawyer;
    }

    public Lawyer findById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection(COLLECTION).document(id).get().get();
        if (snapshot.exists()) {
            return fromDocument(snapshot);
        }
        return null;
    }

    public List<Lawyer> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION).get();
        List<Lawyer> lawyers = new ArrayList<>();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            lawyers.add(fromDocument(document));
        }
        return lawyers;
    }

    public List<Lawyer> findByStatus(String status) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION)
                .whereEqualTo("status", status)
                .get();
        List<Lawyer> lawyers = new ArrayList<>();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            lawyers.add(fromDocument(document));
        }
        return lawyers;
    }

    public List<Lawyer> findByPracticeAreaAndStatus(String practiceArea, String status) 
            throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION)
                .whereEqualTo("status", status)
                .whereArrayContains("practiceAreas", practiceArea)
                .get();
        List<Lawyer> lawyers = new ArrayList<>();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            lawyers.add(fromDocument(document));
        }
        return lawyers;
    }

    public List<Lawyer> findByLanguageAndStatus(String language, String status) 
            throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION)
                .whereEqualTo("status", status)
                .whereArrayContains("languages", language)
                .get();
        List<Lawyer> lawyers = new ArrayList<>();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            lawyers.add(fromDocument(document));
        }
        return lawyers;
    }

    private Lawyer fromDocument(DocumentSnapshot document) {
        Lawyer lawyer = new Lawyer(document.getData());
        lawyer.setId(document.getId());
        return lawyer;
    }

    public Lawyer update(Lawyer lawyer) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION)
                .document(lawyer.getId())
                .set(lawyer.toMap());
        future.get();
        return lawyer;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION).document(id).delete();
        future.get();
    }
}
