package com.nyayasetu.dto;

public class BookingRequest {
    private String lawyerId;
    private String practiceArea;
    private String mode;
    private Long scheduledStartsAt;
    private Long scheduledEndsAt;
    private String clientNote;

    public BookingRequest() {}

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
    public String getClientNote() { return clientNote; }
    public void setClientNote(String clientNote) { this.clientNote = clientNote; }
}
