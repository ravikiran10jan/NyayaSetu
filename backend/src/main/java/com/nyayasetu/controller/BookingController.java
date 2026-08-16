package com.nyayasetu.controller;

import com.nyayasetu.config.FirebaseUserPrincipal;
import com.nyayasetu.dto.ApiResponse;
import com.nyayasetu.dto.BookingRequest;
import com.nyayasetu.dto.BookingResponse;
import com.nyayasetu.model.AdvocateBooking;
import com.nyayasetu.model.Lawyer;
import com.nyayasetu.service.BookingService;
import com.nyayasetu.service.LawyerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;
    private final LawyerService lawyerService;

    public BookingController(BookingService bookingService, LawyerService lawyerService) {
        this.bookingService = bookingService;
        this.lawyerService = lawyerService;
    }

    @PostMapping("/bookings")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @RequestBody BookingRequest request,
            @AuthenticationPrincipal FirebaseUserPrincipal principal) throws ExecutionException, InterruptedException {
        
        try {
            AdvocateBooking booking = bookingService.createBooking(
                request, principal.getEmail(), principal.getUid());
            return ResponseEntity.status(201).body(
                ApiResponse.success("Booking created successfully", new BookingResponse(booking)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/bookings/my")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(
            @AuthenticationPrincipal FirebaseUserPrincipal principal) throws ExecutionException, InterruptedException {
        
        List<AdvocateBooking> bookings = bookingService.getClientBookings(principal.getUid());
        List<BookingResponse> responses = bookings.stream()
            .map(BookingResponse::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/advocate/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getLawyerBookings(
            @AuthenticationPrincipal FirebaseUserPrincipal principal) throws ExecutionException, InterruptedException {
        
        // Find lawyer by email
        List<Lawyer> allLawyers = lawyerService.getAllLawyers();
        Lawyer lawyer = allLawyers.stream()
            .filter(l -> l.getEmail().equals(principal.getEmail()))
            .findFirst()
            .orElse(null);
        
        if (lawyer == null || !"active".equals(lawyer.getStatus())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Not an active lawyer"));
        }
        
        List<AdvocateBooking> bookings = bookingService.getLawyerBookings(lawyer.getId());
        List<BookingResponse> responses = bookings.stream()
            .map(BookingResponse::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/advocate/bookings/{id}/accept")
    public ResponseEntity<ApiResponse<BookingResponse>> acceptBooking(
            @PathVariable String id,
            @AuthenticationPrincipal FirebaseUserPrincipal principal) throws ExecutionException, InterruptedException {
        
        try {
            AdvocateBooking booking = bookingService.acceptBooking(id, principal.getUid());
            if (booking == null) {
                return ResponseEntity.status(404).body(ApiResponse.error("Booking not found"));
            }
            return ResponseEntity.ok(ApiResponse.success("Booking accepted", new BookingResponse(booking)));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/advocate/bookings/{id}/complete")
    public ResponseEntity<ApiResponse<BookingResponse>> completeBooking(
            @PathVariable String id,
            @AuthenticationPrincipal FirebaseUserPrincipal principal) throws ExecutionException, InterruptedException {
        
        try {
            AdvocateBooking booking = bookingService.completeBooking(id, principal.getUid());
            if (booking == null) {
                return ResponseEntity.status(404).body(ApiResponse.error("Booking not found"));
            }
            return ResponseEntity.ok(ApiResponse.success("Booking completed", new BookingResponse(booking)));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
