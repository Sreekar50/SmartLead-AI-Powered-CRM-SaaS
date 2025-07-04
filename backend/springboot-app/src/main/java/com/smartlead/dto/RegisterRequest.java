// src/main/java/com/smartlead/dto/RegisterRequest.java
package com.smartlead.dto;

public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String companyName;
    private String domain;
    
    
    public RegisterRequest() {}
    
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
}