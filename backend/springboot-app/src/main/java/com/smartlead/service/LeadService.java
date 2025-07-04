// src/main/java/com/smartlead/service/LeadService.java
package com.smartlead.service;

import com.smartlead.entity.Lead;
import com.smartlead.entity.Lead.LeadScore;
import com.smartlead.entity.Tenant;
import com.smartlead.repository.LeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class LeadService {
    
    @Autowired
    private LeadRepository leadRepository;
    
    @Autowired
    private AILeadScoringService aiLeadScoringService;
    
    public Lead createLead(String firstName, String lastName, String email, 
                          String phone, String company, String jobTitle, Tenant tenant) {
        Lead lead = new Lead(firstName, lastName, email, phone, company, jobTitle, tenant);
        
        // AI Lead Scoring
        int aiScore = aiLeadScoringService.calculateLeadScore(lead);
        lead.setScoreValue(aiScore);
        lead.setScore(determineLeadScore(aiScore));
        
        return leadRepository.save(lead);
    }
    
    @Cacheable("leads")
    public List<Lead> getLeadsByTenant(Long tenantId) {
        return leadRepository.findByTenantId(tenantId);
    }
    
    public Optional<Lead> getLeadById(Long id) {
        return leadRepository.findById(id);
    }
    
    public Lead updateLead(Lead lead) {
        return leadRepository.save(lead);
    }
    
    public void deleteLead(Long id) {
        leadRepository.deleteById(id);
    }
    
    public List<Lead> getStaleLeads(Long tenantId, int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        return leadRepository.findStaleLeads(tenantId, cutoffDate);
    }
    
    private LeadScore determineLeadScore(int scoreValue) {
        if (scoreValue >= 80) return LeadScore.HOT;
        if (scoreValue >= 50) return LeadScore.WARM;
        return LeadScore.COLD;
    }
}