package com.nyayasetu.model;

import java.util.List;
import java.util.Map;

public class Lawyer {
    private String id;
    private String name;
    private String email;
    private String barCouncil;
    private String enrolmentNumber;
    private String jurisdictionState;
    private String city;
    private List<String> practiceAreas;
    private List<String> languages;
    private String verificationNote;
    private Long verifiedAt;
    private String status; // pending, active, rejected, suspended
    private String reviewedBy;
    private Long reviewedAt;
    private Long createdAt;
    private Long updatedAt;
    private Double rating;
    private Integer yearsOfExperience;
    private Double pricePerMinute;
    private Boolean isAvailable;

    public Lawyer() {}

    public Lawyer(Map<String, Object> map) {
        this.id = (String) map.get("id");
        this.name = (String) map.get("name");
        this.email = (String) map.get("email");
        this.barCouncil = (String) map.get("barCouncil");
        this.enrolmentNumber = (String) map.get("enrolmentNumber");
        this.jurisdictionState = (String) map.get("jurisdictionState");
        this.city = (String) map.get("city");
        this.practiceAreas = (List<String>) map.get("practiceAreas");
        this.languages = (List<String>) map.get("languages");
        this.verificationNote = (String) map.get("verificationNote");
        Object va = map.get("verifiedAt");
        this.verifiedAt = va instanceof Number ? ((Number) va).longValue() : null;
        this.status = (String) map.get("status");
        this.reviewedBy = (String) map.get("reviewedBy");
        Object ra = map.get("reviewedAt");
        this.reviewedAt = ra instanceof Number ? ((Number) ra).longValue() : null;
        Object ca = map.get("createdAt");
        this.createdAt = ca instanceof Number ? ((Number) ca).longValue() : null;
        Object ua = map.get("updatedAt");
        this.updatedAt = ua instanceof Number ? ((Number) ua).longValue() : null;
        Object rt = map.get("rating");
        this.rating = rt instanceof Number ? ((Number) rt).doubleValue() : null;
        Object ye = map.get("yearsOfExperience");
        this.yearsOfExperience = ye instanceof Number ? ((Number) ye).intValue() : null;
        Object pp = map.get("pricePerMinute");
        this.pricePerMinute = pp instanceof Number ? ((Number) pp).doubleValue() : null;
        Object av = map.get("isAvailable");
        this.isAvailable = av instanceof Boolean ? (Boolean) av : null;
    }

    public Map<String, Object> toMap() {
        return Map.ofEntries(
            Map.entry("id", id),
            Map.entry("name", name),
            Map.entry("email", email),
            Map.entry("barCouncil", barCouncil),
            Map.entry("enrolmentNumber", enrolmentNumber),
            Map.entry("jurisdictionState", jurisdictionState),
            Map.entry("city", city),
            Map.entry("practiceAreas", practiceAreas),
            Map.entry("languages", languages),
            Map.entry("verificationNote", verificationNote != null ? verificationNote : ""),
            Map.entry("verifiedAt", verifiedAt != null ? verifiedAt : 0L),
            Map.entry("status", status),
            Map.entry("reviewedBy", reviewedBy != null ? reviewedBy : ""),
            Map.entry("reviewedAt", reviewedAt != null ? reviewedAt : 0L),
            Map.entry("createdAt", createdAt != null ? createdAt : 0L),
            Map.entry("updatedAt", updatedAt != null ? updatedAt : 0L),
            Map.entry("rating", rating != null ? rating : 0.0),
            Map.entry("yearsOfExperience", yearsOfExperience != null ? yearsOfExperience : 0),
            Map.entry("pricePerMinute", pricePerMinute != null ? pricePerMinute : 0.0),
            Map.entry("isAvailable", isAvailable != null ? isAvailable : true)
        );
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
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
    public String getVerificationNote() { return verificationNote; }
    public void setVerificationNote(String verificationNote) { this.verificationNote = verificationNote; }
    public Long getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(Long verifiedAt) { this.verifiedAt = verifiedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
    public Long getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Long reviewedAt) { this.reviewedAt = reviewedAt; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public Double getPricePerMinute() { return pricePerMinute; }
    public void setPricePerMinute(Double pricePerMinute) { this.pricePerMinute = pricePerMinute; }
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
}
