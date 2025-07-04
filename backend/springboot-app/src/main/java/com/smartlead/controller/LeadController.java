// src/main/java/com/smartlead/controller/LeadController.java
package com.smartlead.controller;

import com.smartlead.dto.LeadDto;
import com.smartlead.dto.CreateLeadRequest;
import com.smartlead.service.LeadService;
import com.smartlead.entity.Lead;
import com.smartlead.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leads")
@CrossOrigin(origins = "http://localhost:3000")
public class LeadController {
    
    @Autowired
    private LeadService leadService;
    
    @GetMapping
    public ResponseEntity<List<LeadDto>> getLeads(@AuthenticationPrincipal User user) {
        List<Lead> leads = leadService.getLeadsByTenant(user.getTenant().getId());
        List<LeadDto> leadDtos = leads.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(leadDtos);
    }
    
    @PostMapping
    public ResponseEntity<LeadDto> createLead(@RequestBody CreateLeadRequest request, 
                                             @AuthenticationPrincipal User user) {
        Lead lead = leadService.createLead(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPhone(),
            request.getCompany(),
            request.getJobTitle(),
            user.getTenant()
        );
        return ResponseEntity.ok(convertToDto(lead));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LeadDto> getLead(@PathVariable Long id) {
        return leadService.getLeadById(id)
                .map(lead -> ResponseEntity.ok(convertToDto(lead)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LeadDto> updateLead(@PathVariable Long id, 
                                             @RequestBody LeadDto leadDto) {
        return leadService.getLeadById(id)
                .map(lead -> {
                    updateLeadFromDto(lead, leadDto);
                    Lead updatedLead = leadService.updateLead(lead);
                    return ResponseEntity.ok(convertToDto(updatedLead));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.ok().build();
    }
    
    private LeadDto convertToDto(Lead lead) {
        LeadDto dto = new LeadDto();
        dto.setId(lead.getId());
        dto.setFirstName(lead.getFirstName());
        dto.setLastName(lead.getLastName());
        dto.setEmail(lead.getEmail());
        dto.setPhone(lead.getPhone());
        dto.setCompany(lead.getCompany());
        dto.setJobTitle(lead.getJobTitle());
        dto.setStatus(lead.getStatus().name());
        dto.setScore(lead.getScore().name());
        dto.setScoreValue(lead.getScoreValue());
        dto.setCreatedAt(lead.getCreatedAt());
        dto.setLastContactedAt(lead.getLastContactedAt());
        return dto;
    }
    
    private void updateLeadFromDto(Lead lead, LeadDto dto) {
        lead.setFirstName(dto.getFirstName());
        lead.setLastName(dto.getLastName());
        lead.setEmail(dto.getEmail());
        lead.setPhone(dto.getPhone());
        lead.setCompany(dto.getCompany());
        lead.setJobTitle(dto.getJobTitle());
        if (dto.getStatus() != null) {
            lead.setStatus(Lead.LeadStatus.valueOf(dto.getStatus()));
        }
    }
}