package com.nyayasetu.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.nyayasetu.model.AdvocateBooking;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class BookingRepository {

    private final Firestore firestore;
    private static final String COLLECTION = "advocateBookings";

    public BookingRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public AdvocateBooking save(AdvocateBooking booking) throws ExecutionException, InterruptedException {
        if (booking.getId() == null || booking.getId().isEmpty()) {
            DocumentReference docRef = firestore.collection(COLLECTION).document();
            booking.setId(docRef.getId());
            ApiFuture<WriteResult> future = docRef.set(booking.toMap());
            future.get();
        } else {
            ApiFuture<WriteResult> future = firestore.collection(COLLECTION)
                    .document(booking.getId())
                    .set(booking.toMap());
            future.get();
        }
        return booking;
    }

    public AdvocateBooking findById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection(COLLECTION).document(id).get().get();
        if (snapshot.exists()) {
            return new AdvocateBooking(snapshot.getData());
        }
        return null;
    }

    public List<AdvocateBooking> findByClientUserId(String clientUserId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION)
                .whereEqualTo("clientUserId", clientUserId)
                .orderBy("scheduledStartsAt", Query.Direction.DESCENDING)
                .get();
        List<AdvocateBooking> bookings = new ArrayList<>();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            bookings.add(new AdvocateBooking(document.getData()));
        }
        return bookings;
    }

    public List<AdvocateBooking> findByLawyerId(String lawyerId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION)
                .whereEqualTo("lawyerId", lawyerId)
                .orderBy("scheduledStartsAt", Query.Direction.DESCENDING)
                .get();
        List<AdvocateBooking> bookings = new ArrayList<>();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            bookings.add(new AdvocateBooking(document.getData()));
        }
        return bookings;
    }

    public List<AdvocateBooking> findConflictingBookings(String lawyerId, Long scheduledStartsAt, Long scheduledEndsAt) 
            throws ExecutionException, InterruptedException {
        // Find bookings where status is requested or accepted AND time overlaps
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION)
                .whereEqualTo("lawyerId", lawyerId)
                .whereIn("status", List.of("requested", "accepted"))
                .whereLessThanOrEqualTo("scheduledStartsAt", scheduledEndsAt)
                .whereGreaterThanOrEqualTo("scheduledEndsAt", scheduledStartsAt)
                .get();
        List<AdvocateBooking> bookings = new ArrayList<>();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            bookings.add(new AdvocateBooking(document.getData()));
        }
        return bookings;
    }

    public AdvocateBooking update(AdvocateBooking booking) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION)
                .document(booking.getId())
                .set(booking.toMap());
        future.get();
        return booking;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION).document(id).delete();
        future.get();
    }
}
