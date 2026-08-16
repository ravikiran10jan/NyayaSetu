package com.nyayasetu.bdd.steps;

import com.nyayasetu.dto.BookingRequest;
import com.nyayasetu.model.AdvocateBooking;
import com.nyayasetu.model.Lawyer;
import com.nyayasetu.repository.BookingRepository;
import com.nyayasetu.repository.LawyerRepository;
import com.nyayasetu.service.AuditService;
import com.nyayasetu.service.BookingService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class BookingSteps {

    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private LawyerRepository lawyerRepository;
    private AuditService auditService;

    private AdvocateBooking booking;

    @Before
    public void setUp() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        lawyerRepository = Mockito.mock(LawyerRepository.class);
        auditService = Mockito.mock(AuditService.class);
        bookingService = new BookingService(bookingRepository, lawyerRepository, auditService);
    }

    @Given("an active lawyer with id {string} and email {string}")
    public void anActiveLawyerWithIdAndEmail(String lawyerId, String email) throws Exception {
        Lawyer lawyer = new Lawyer();
        lawyer.setId(lawyerId);
        lawyer.setEmail(email);
        lawyer.setStatus("active");
        Mockito.when(lawyerRepository.findById(lawyerId)).thenReturn(lawyer);
        Mockito.when(bookingRepository.findConflictingBookings(anyString(), anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());
    }

    @Given("the current time is well within booking window")
    public void theCurrentTimeIsWellWithinBookingWindow() {
        // No action needed - time is set in When step
    }

    @When("a client with email {string} and userId {string} books lawyer {string} for a chat")
    public void aClientWithEmailAndUserIdBooksLawyerForChat(String clientEmail, String clientUserId, String lawyerId) {
        try {
            BookingRequest request = new BookingRequest();
            request.setLawyerId(lawyerId);
            request.setMode("chat");
            request.setScheduledStartsAt(System.currentTimeMillis() + 30 * 60 * 1000); // 30 min from now
            request.setScheduledEndsAt(System.currentTimeMillis() + 60 * 60 * 1000); // 60 min from now

            Mockito.when(bookingRepository.save(any(AdvocateBooking.class))).thenAnswer(invocation -> {
                AdvocateBooking b = invocation.getArgument(0);
                b.setId("generated-booking-id");
                return b;
            });

            booking = bookingService.createBooking(request, clientEmail, clientUserId);
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Then("the booking is created with status {string}")
    public void theBookingIsCreatedWithStatus(String expectedStatus) {
        assertNull(TestContext.getThrownException(), "Expected no exception but got: " + TestContext.getThrownException());
        assertNotNull(booking);
        assertEquals(expectedStatus, booking.getStatus());
    }

    @Then("the booking has clientEmail {string}")
    public void theBookingHasClientEmail(String expectedEmail) {
        assertEquals(expectedEmail, booking.getClientEmail());
    }

    @Then("the booking has mode {string}")
    public void theBookingHasMode(String expectedMode) {
        assertEquals(expectedMode, booking.getMode());
    }

    @Given("a pending lawyer with id {string}")
    public void aPendingLawyerWithId(String lawyerId) throws Exception {
        Lawyer lawyer = new Lawyer();
        lawyer.setId(lawyerId);
        lawyer.setStatus("pending");
        Mockito.when(lawyerRepository.findById(lawyerId)).thenReturn(lawyer);
    }

    @When("a client tries to book lawyer {string}")
    public void aClientTriesToBookLawyer(String lawyerId) {
        try {
            BookingRequest request = new BookingRequest();
            request.setLawyerId(lawyerId);
            request.setMode("chat");
            request.setScheduledStartsAt(System.currentTimeMillis() + 30 * 60 * 1000);
            request.setScheduledEndsAt(System.currentTimeMillis() + 60 * 60 * 1000);

            booking = bookingService.createBooking(request, "client@example.com", "client-1");
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Then("an IllegalArgumentException is thrown with message containing {string}")
    public void anIllegalArgumentExceptionIsThrownWithMessageContaining(String messagePart) {
        assertNotNull(TestContext.getThrownException(), "Expected an exception but none was thrown");
        assertTrue(TestContext.getThrownException() instanceof IllegalArgumentException,
                "Expected IllegalArgumentException but got: " + TestContext.getThrownException().getClass().getName());
        assertTrue(TestContext.getThrownException().getMessage().contains(messagePart),
                "Expected message to contain '" + messagePart + "' but got: " + TestContext.getThrownException().getMessage());
    }

    @When("a client books lawyer {string} with a start time only 5 minutes from now")
    public void aClientBooksLawyerWithStartTimeOnly5MinutesFromNow(String lawyerId) {
        try {
            BookingRequest request = new BookingRequest();
            request.setLawyerId(lawyerId);
            request.setMode("chat");
            request.setScheduledStartsAt(System.currentTimeMillis() + 5 * 60 * 1000); // 5 min from now
            request.setScheduledEndsAt(System.currentTimeMillis() + 35 * 60 * 1000);

            booking = bookingService.createBooking(request, "client@example.com", "client-1");
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Given("lawyer {string} already has a conflicting booking")
    public void lawyerAlreadyHasAConflictingBooking(String lawyerId) throws Exception {
        AdvocateBooking conflict = new AdvocateBooking();
        conflict.setId("conflict-booking");
        conflict.setLawyerId(lawyerId);
        conflict.setStatus("requested");
        conflict.setScheduledStartsAt(System.currentTimeMillis() + 30 * 60 * 1000);
        conflict.setScheduledEndsAt(System.currentTimeMillis() + 60 * 60 * 1000);

        Mockito.when(bookingRepository.findConflictingBookings(eq(lawyerId), anyLong(), anyLong()))
                .thenReturn(List.of(conflict));
    }

    @When("a client tries to book lawyer {string} at the same time")
    public void aClientTriesToBookLawyerAtTheSameTime(String lawyerId) {
        try {
            BookingRequest request = new BookingRequest();
            request.setLawyerId(lawyerId);
            request.setMode("chat");
            request.setScheduledStartsAt(System.currentTimeMillis() + 30 * 60 * 1000);
            request.setScheduledEndsAt(System.currentTimeMillis() + 60 * 60 * 1000);

            booking = bookingService.createBooking(request, "client@example.com", "client-1");
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Then("an IllegalStateException is thrown with message containing {string}")
    public void anIllegalStateExceptionIsThrownWithMessageContaining(String messagePart) {
        assertNotNull(TestContext.getThrownException(), "Expected an exception but none was thrown");
        assertTrue(TestContext.getThrownException() instanceof IllegalStateException,
                "Expected IllegalStateException but got: " + TestContext.getThrownException().getClass().getName());
        assertTrue(TestContext.getThrownException().getMessage().contains(messagePart),
                "Expected message to contain '" + messagePart + "' but got: " + TestContext.getThrownException().getMessage());
    }

    @Given("a booking with id {string} in status {string} assigned to lawyer with email {string} and id {string}")
    public void aBookingWithIdInStatusAssignedToLawyerWithEmailAndId(String bookingId, String status, String lawyerEmail, String lawyerId) throws Exception {
        booking = new AdvocateBooking();
        booking.setId(bookingId);
        booking.setStatus(status);
        booking.setLawyerId(lawyerId);
        booking.setVersion(1L);
        booking.setScheduledStartsAt(System.currentTimeMillis() - 60 * 60 * 1000); // Past start time

        Lawyer lawyer = new Lawyer();
        lawyer.setId(lawyerId);
        lawyer.setEmail(lawyerEmail);
        lawyer.setStatus("active");

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(booking);
        Mockito.when(lawyerRepository.findById(lawyerId)).thenReturn(lawyer);
        Mockito.when(bookingRepository.update(any(AdvocateBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @When("the advocate with email {string} accepts booking {string}")
    public void theAdvocateWithEmailAcceptsBooking(String lawyerEmail, String bookingId) {
        try {
            booking = bookingService.acceptBooking(bookingId, lawyerEmail);
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Then("the booking status is {string}")
    public void theBookingStatusIs(String expectedStatus) {
        assertNull(TestContext.getThrownException(), "Expected no exception but got: " + TestContext.getThrownException());
        assertNotNull(booking);
        assertEquals(expectedStatus, booking.getStatus());
    }

    @Then("the booking acceptedAt is set")
    public void theBookingAcceptedAtIsSet() {
        assertNotNull(booking.getAcceptedAt());
    }

    @When("the advocate with email {string} tries to accept booking {string}")
    public void theAdvocateWithEmailTriesToAcceptBooking(String lawyerEmail, String bookingId) {
        try {
            booking = bookingService.acceptBooking(bookingId, lawyerEmail);
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Then("a SecurityException is thrown")
    public void aSecurityExceptionIsThrown() {
        assertNotNull(TestContext.getThrownException(), "Expected a SecurityException but none was thrown");
        assertTrue(TestContext.getThrownException() instanceof SecurityException,
                "Expected SecurityException but got: " + TestContext.getThrownException().getClass().getName());
    }

    @Given("a booking with id {string} in status {string} with past scheduled start assigned to lawyer with email {string} and id {string}")
    public void aBookingWithIdInStatusWithPastScheduledStartAssignedToLawyerWithEmailAndId(String bookingId, String status, String lawyerEmail, String lawyerId) throws Exception {
        booking = new AdvocateBooking();
        booking.setId(bookingId);
        booking.setStatus(status);
        booking.setLawyerId(lawyerId);
        booking.setVersion(1L);
        booking.setScheduledStartsAt(System.currentTimeMillis() - 60 * 60 * 1000); // Past

        Lawyer lawyer = new Lawyer();
        lawyer.setId(lawyerId);
        lawyer.setEmail(lawyerEmail);
        lawyer.setStatus("active");

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(booking);
        Mockito.when(lawyerRepository.findById(lawyerId)).thenReturn(lawyer);
        Mockito.when(bookingRepository.update(any(AdvocateBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @When("the advocate with email {string} completes booking {string}")
    public void theAdvocateWithEmailCompletesBooking(String lawyerEmail, String bookingId) {
        try {
            booking = bookingService.completeBooking(bookingId, lawyerEmail);
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }

    @Given("a booking with id {string} in status {string} with future scheduled start assigned to lawyer with email {string} and id {string}")
    public void aBookingWithIdInStatusWithFutureScheduledStartAssignedToLawyerWithEmailAndId(String bookingId, String status, String lawyerEmail, String lawyerId) throws Exception {
        booking = new AdvocateBooking();
        booking.setId(bookingId);
        booking.setStatus(status);
        booking.setLawyerId(lawyerId);
        booking.setVersion(1L);
        booking.setScheduledStartsAt(System.currentTimeMillis() + 60 * 60 * 1000); // Future

        Lawyer lawyer = new Lawyer();
        lawyer.setId(lawyerId);
        lawyer.setEmail(lawyerEmail);
        lawyer.setStatus("active");

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(booking);
        Mockito.when(lawyerRepository.findById(lawyerId)).thenReturn(lawyer);
    }

    @When("the advocate with email {string} tries to complete booking {string}")
    public void theAdvocateWithEmailTriesToCompleteBooking(String lawyerEmail, String bookingId) {
        try {
            booking = bookingService.completeBooking(bookingId, lawyerEmail);
        } catch (Exception e) {
            TestContext.setThrownException(e);
        }
    }
}
