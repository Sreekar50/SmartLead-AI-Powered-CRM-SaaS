// src/main/java/com/smartlead/dto/LoginResponse.java
package com.smartlead.dto;

public class LoginResponse {
    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String tenantId;
    
    
    public LoginResponse() {}
    
    public LoginResponse(String token, String email, String firstName, 
                        String lastName, String role, String tenantId) {
        this.token = token;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.tenantId = tenantId;
    }
    
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}