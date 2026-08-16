package com.nyayasetu.service;

import com.nyayasetu.dto.BookingRequest;
import com.nyayasetu.model.AdvocateBooking;
import com.nyayasetu.model.Lawyer;
import com.nyayasetu.repository.BookingRepository;
import com.nyayasetu.repository.LawyerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final LawyerRepository lawyerRepository;
    private final AuditService auditService;

    public BookingService(BookingRepository bookingRepository, LawyerRepository lawyerRepository, 
                         AuditService auditService) {
        this.bookingRepository = bookingRepository;
        this.lawyerRepository = lawyerRepository;
        this.auditService = auditService;
    }

    public AdvocateBooking createBooking(BookingRequest request, String clientEmail, String clientUserId) 
            throws ExecutionException, InterruptedException {
        
        // Validate lawyer exists and is active
        Lawyer lawyer = lawyerRepository.findById(request.getLawyerId());
        if (lawyer == null) {
            throw new IllegalArgumentException("Lawyer not found");
        }
        if (!"active".equals(lawyer.getStatus())) {
            throw new IllegalArgumentException("Lawyer is not active");
        }

        // Validate time range (15 minutes to 90 days from now)
        long now = System.currentTimeMillis();
        long minTime = now + (15 * 60 * 1000); // 15 minutes
        long maxTime = now + (90L * 24 * 60 * 60 * 1000); // 90 days
        
        if (request.getScheduledStartsAt() < minTime || request.getScheduledStartsAt() > maxTime) {
            throw new IllegalArgumentException("Scheduled time must be between 15 minutes and 90 days from now");
        }
        if (request.getScheduledEndsAt() <= request.getScheduledStartsAt()) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Check for slot conflicts
        List<AdvocateBooking> conflicts = bookingRepository.findConflictingBookings(
            request.getLawyerId(), 
            request.getScheduledStartsAt(), 
            request.getScheduledEndsAt()
        );
        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Time slot conflict: lawyer already has a booking in this time range");
        }

        // Create booking
        AdvocateBooking booking = new AdvocateBooking();
        booking.setClientEmail(clientEmail);
        booking.setClientUserId(clientUserId);
        booking.setLawyerId(request.getLawyerId());
        booking.setPracticeArea(request.getPracticeArea());
        booking.setMode(request.getMode());
        booking.setScheduledStartsAt(request.getScheduledStartsAt());
        booking.setScheduledEndsAt(request.getScheduledEndsAt());
        booking.setClientNote(request.getClientNote());
        booking.setStatus("requested");
        booking.setVersion(1L);
        booking.setCreatedAt(now);
        booking.setUpdatedAt(now);

        booking = bookingRepository.save(booking);

        // Log audit event
        auditService.logEvent(clientUserId, clientEmail, "booking_created", "booking", 
                             booking.getId(), "Booking created for lawyer " + lawyer.getName());

        return booking;
    }

    public List<AdvocateBooking> getClientBookings(String clientUserId) throws ExecutionException, InterruptedException {
        return bookingRepository.findByClientUserId(clientUserId);
    }

    public List<AdvocateBooking> getLawyerBookings(String lawyerId) throws ExecutionException, InterruptedException {
        return bookingRepository.findByLawyerId(lawyerId);
    }

    public AdvocateBooking acceptBooking(String bookingId, String lawyerUserId) 
            throws ExecutionException, InterruptedException {
        AdvocateBooking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            return null;
        }
        
        // Verify lawyer owns this booking
        Lawyer lawyer = lawyerRepository.findById(booking.getLawyerId());
        if (lawyer == null || !lawyer.getEmail().equals(lawyerUserId)) {
            throw new SecurityException("Not authorized to accept this booking");
        }
        
        if (!"requested".equals(booking.getStatus())) {
            throw new IllegalStateException("Booking can only be accepted when in requested status");
        }

        booking.setStatus("accepted");
        booking.setAcceptedAt(System.currentTimeMillis());
        booking.setUpdatedAt(System.currentTimeMillis());
        booking.setVersion(booking.getVersion() + 1);

        booking = bookingRepository.update(booking);

        auditService.logEvent(lawyerUserId, lawyer.getEmail(), "booking_accepted", "booking", 
                             booking.getId(), "Booking accepted");

        return booking;
    }

    public AdvocateBooking completeBooking(String bookingId, String lawyerUserId) 
            throws ExecutionException, InterruptedException {
        AdvocateBooking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            return null;
        }
        
        // Verify lawyer owns this booking
        Lawyer lawyer = lawyerRepository.findById(booking.getLawyerId());
        if (lawyer == null || !lawyer.getEmail().equals(lawyerUserId)) {
            throw new SecurityException("Not authorized to complete this booking");
        }
        
        if (!"accepted".equals(booking.getStatus())) {
            throw new IllegalStateException("Booking can only be completed when in accepted status");
        }
        
        if (booking.getScheduledStartsAt() > System.currentTimeMillis()) {
            throw new IllegalStateException("Cannot complete booking before scheduled start time");
        }

        booking.setStatus("completed");
        booking.setCompletedAt(System.currentTimeMillis());
        booking.setUpdatedAt(System.currentTimeMillis());
        booking.setVersion(booking.getVersion() + 1);

        booking = bookingRepository.update(booking);

        auditService.logEvent(lawyerUserId, lawyer.getEmail(), "booking_completed", "booking", 
                             booking.getId(), "Booking completed");

        return booking;
    }

    public AdvocateBooking getBookingById(String id) throws ExecutionException, InterruptedException {
        return bookingRepository.findById(id);
    }
}
