// src/main/java/com/smartlead/service/AuthService.java
package com.smartlead.service;

import com.smartlead.dto.LoginResponse;
import com.smartlead.dto.RegisterRequest;
import com.smartlead.entity.Tenant;
import com.smartlead.entity.User;
import com.smartlead.repository.UserRepository;
import com.smartlead.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TenantService tenantService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getTenant().getTenantId());
        
        return new LoginResponse(
            token,
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole().name(),
            user.getTenant().getTenantId()
        );
    }
    
    public void register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        
        // Create or find tenant
        Tenant tenant = tenantService.findByDomain(request.getDomain())
                .orElseGet(() -> tenantService.createTenant(request.getCompanyName(), request.getDomain()));
        
        // Create user
        User user = new User(
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getFirstName(),
            request.getLastName(),
            User.Role.ADMIN, // First user is admin
            tenant
        );
        
        userRepository.save(user);
    }
}