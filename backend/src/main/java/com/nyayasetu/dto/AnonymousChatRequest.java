package com.nyayasetu.dto;

public class AnonymousChatRequest {
    private String alias;
    private String phoneMask;
    private String phoneHash;
    private Boolean consent;
    private String lawyerId;

    public AnonymousChatRequest() {}

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public String getPhoneMask() { return phoneMask; }
    public void setPhoneMask(String phoneMask) { this.phoneMask = phoneMask; }
    public String getPhoneHash() { return phoneHash; }
    public void setPhoneHash(String phoneHash) { this.phoneHash = phoneHash; }
    public Boolean getConsent() { return consent; }
    public void setConsent(Boolean consent) { this.consent = consent; }
    public String getLawyerId() { return lawyerId; }
    public void setLawyerId(String lawyerId) { this.lawyerId = lawyerId; }
}
