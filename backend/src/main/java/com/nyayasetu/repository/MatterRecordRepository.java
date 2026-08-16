package com.nyayasetu.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.nyayasetu.model.MatterRecord;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class MatterRecordRepository {

    private final Firestore firestore;
    private static final String COLLECTION = "matterRecords";

    public MatterRecordRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public MatterRecord save(MatterRecord record) throws ExecutionException, InterruptedException {
        if (record.getId() == null || record.getId().isEmpty()) {
            DocumentReference docRef = firestore.collection(COLLECTION).document();
            record.setId(docRef.getId());
            ApiFuture<WriteResult> future = docRef.set(record.toMap());
            future.get();
        } else {
            ApiFuture<WriteResult> future = firestore.collection(COLLECTION)
                    .document(record.getId())
                    .set(record.toMap());
            future.get();
        }
        return record;
    }

    public MatterRecord findById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection(COLLECTION).document(id).get().get();
        if (snapshot.exists()) {
            return new MatterRecord(snapshot.getData());
        }
        return null;
    }

    public List<MatterRecord> findByMatterId(String matterId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION)
                .whereEqualTo("matterId", matterId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get();
        List<MatterRecord> records = new ArrayList<>();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            records.add(new MatterRecord(document.getData()));
        }
        return records;
    }

    public MatterRecord update(MatterRecord record) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION)
                .document(record.getId())
                .set(record.toMap());
        future.get();
        return record;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION).document(id).delete();
        future.get();
    }
}
