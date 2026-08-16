package com.nyayasetu.dto;

public class MatterRequest {
    private String title;
    private String practiceArea;
    private String state;
    private String language;
    private String urgency;
    private String opposingParty;
    private String lawyerId;

    public MatterRequest() {}

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
    public String getLawyerId() { return lawyerId; }
    public void setLawyerId(String lawyerId) { this.lawyerId = lawyerId; }
}
