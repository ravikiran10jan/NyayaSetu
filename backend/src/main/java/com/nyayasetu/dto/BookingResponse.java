package com.nyayasetu.dto;

import com.nyayasetu.model.AdvocateBooking;

public class BookingResponse {
    private String id;
    private String clientEmail;
    private String clientUserId;
    private String clientAlias;
    private String lawyerId;
    private String practiceArea;
    private String mode;
    private Long scheduledStartsAt;
    private Long scheduledEndsAt;
    private String status;
    private String clientNote;
    private Long acceptedAt;
    private Long completedAt;
    private Long version;
    private Long createdAt;
    private Long updatedAt;

    public BookingResponse() {}

    public BookingResponse(AdvocateBooking booking) {
        this.id = booking.getId();
        this.clientEmail = booking.getClientEmail();
        this.clientUserId = booking.getClientUserId();
        this.clientAlias = booking.getClientAlias();
        this.lawyerId = booking.getLawyerId();
        this.practiceArea = booking.getPracticeArea();
        this.mode = booking.getMode();
        this.scheduledStartsAt = booking.getScheduledStartsAt();
        this.scheduledEndsAt = booking.getScheduledEndsAt();
        this.status = booking.getStatus();
        this.clientNote = booking.getClientNote();
        this.acceptedAt = booking.getAcceptedAt();
        this.completedAt = booking.getCompletedAt();
        this.version = booking.getVersion();
        this.createdAt = booking.getCreatedAt();
        this.updatedAt = booking.getUpdatedAt();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }
    public String getClientUserId() { return clientUserId; }
    public void setClientUserId(String clientUserId) { this.clientUserId = clientUserId; }
    public String getClientAlias() { return clientAlias; }
    public void setClientAlias(String clientAlias) { this.clientAlias = clientAlias; }
    public String getLawyerId() { return lawyerId; }
    public void setLawyerId(String lawyerId) { this.lawyerId = lawyerId; }
    public String getPracticeArea() { return practiceArea; }
    public void setPracticeArea(String practiceArea) { this.practiceArea = practiceArea; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public Long getScheduledStartsAt() { return scheduledStartsAt; }
    public void setScheduledStartsAt(Long scheduledStartsAt) { this.scheduledStartsAt = scheduledStartsAt; }
    public Long getScheduledEndsAt() { return scheduledEndsAt; }
    public void setScheduledEndsAt(Long scheduledEndsAt) { this.scheduledEndsAt = scheduledEndsAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getClientNote() { return clientNote; }
    public void setClientNote(String clientNote) { this.clientNote = clientNote; }
    public Long getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(Long acceptedAt) { this.acceptedAt = acceptedAt; }
    public Long getCompletedAt() { return completedAt; }
    public void setCompletedAt(Long completedAt) { this.completedAt = completedAt; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
