package com.nyayasetu.dto;

import java.util.List;

public class LawyerApplicationRequest {
    private String name;
    private String email;
    private String barCouncil;
    private String enrolmentNumber;
    private String jurisdictionState;
    private String city;
    private List<String> practiceAreas;
    private List<String> languages;

    public LawyerApplicationRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getBarCouncil() { return barCouncil; }
    public void setBarCouncil(String barCouncil) { this.barCouncil = barCouncil; }
    public String getEnrolmentNumber() { return enrolmentNumber; }
    public void setEnrolmentNumber(String enrolmentNumber) { this.enrolmentNumber = enrolmentNumber; }
    public String getJurisdictionState() { return jurisdictionState; }
    public void setJurisdictionState(String jurisdictionState) { this.jurisdictionState = jurisdictionState; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public List<String> getPracticeAreas() { return practiceAreas; }
    public void setPracticeAreas(List<String> practiceAreas) { this.practiceAreas = practiceAreas; }
    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }
}
