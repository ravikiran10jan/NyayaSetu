package com.nyayasetu.dto;

import com.nyayasetu.model.LegalMatter;

public class MatterResponse {
    private String id;
    private String ownerEmail;
    private String ownerUserId;
    private String lawyerId;
    private String title;
    private String practiceArea;
    private String state;
    private String language;
    private String urgency;
    private String opposingParty;
    private String conflictStatus;
    private String status;
    private String cnrNumber;
    private Long createdAt;
    private Long updatedAt;

    public MatterResponse() {}

    public MatterResponse(LegalMatter matter) {
        this.id = matter.getId();
        this.ownerEmail = matter.getOwnerEmail();
        this.ownerUserId = matter.getOwnerUserId();
        this.lawyerId = matter.getLawyerId();
        this.title = matter.getTitle();
        this.practiceArea = matter.getPracticeArea();
        this.state = matter.getState();
        this.language = matter.getLanguage();
        this.urgency = matter.getUrgency();
        this.opposingParty = matter.getOpposingParty();
        this.conflictStatus = matter.getConflictStatus();
        this.status = matter.getStatus();
        this.cnrNumber = matter.getCnrNumber();
        this.createdAt = matter.getCreatedAt();
        this.updatedAt = matter.getUpdatedAt();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
    public String getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(String ownerUserId) { this.ownerUserId = ownerUserId; }
    public String getLawyerId() { return lawyerId; }
    public void setLawyerId(String lawyerId) { this.lawyerId = lawyerId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPracticeArea() { return practiceArea; }
    public void setPracticeArea(String practiceArea) { this.practiceArea = practiceArea; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    public String getOpposingParty() { return opposingParty; }
    public void setOpposingParty(String opposingParty) { this.opposingParty = opposingParty; }
    public String getConflictStatus() { return conflictStatus; }
    public void setConflictStatus(String conflictStatus) { this.conflictStatus = conflictStatus; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCnrNumber() { return cnrNumber; }
    public void setCnrNumber(String cnrNumber) { this.cnrNumber = cnrNumber; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
