// src/main/java/com/smartlead/service/AILeadScoringService.java
package com.smartlead.service;

import com.smartlead.entity.Lead;
import org.springframework.stereotype.Service;

@Service
public class AILeadScoringService {
    
    public int calculateLeadScore(Lead lead) {
        // Simple rule-based scoring algorithm
        // In production, integrate with OpenAI API or ML model
        int score = 0;
        
        // Email domain scoring
        if (lead.getEmail().contains("@gmail.com") || lead.getEmail().contains("@yahoo.com")) {
            score += 10;
        } else {
            score += 25; // Business email
        }
        
        // Company presence
        if (lead.getCompany() != null && !lead.getCompany().isEmpty()) {
            score += 20;
        }
        
        // Job title scoring
        if (lead.getJobTitle() != null) {
            String title = lead.getJobTitle().toLowerCase();
            if (title.contains("ceo") || title.contains("founder") || title.contains("director")) {
                score += 30;
            } else if (title.contains("manager") || title.contains("lead")) {
                score += 20;
            } else {
                score += 10;
            }
        }
        
        // Phone number presence
        if (lead.getPhone() != null && !lead.getPhone().isEmpty()) {
            score += 15;
        }
        
        return Math.min(score, 100);
    }
}