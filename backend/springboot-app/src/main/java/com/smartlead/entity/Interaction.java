// src/main/java/com/smartlead/entity/Interaction.java
package com.smartlead.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interactions")
public class Interaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private InteractionType type;
    
    @Column(nullable = false, length = 1000)
    private String notes;
    
    @ManyToOne
    @JoinColumn(name = "lead_id")
    private Lead lead;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    public enum InteractionType {
        EMAIL, CALL, MEETING, NOTE
    }
    
    
    public Interaction() {}
    
    public Interaction(InteractionType type, String notes, Lead lead, User user) {
        this.type = type;
        this.notes = notes;
        this.lead = lead;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
    
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public InteractionType getType() { return type; }
    public void setType(InteractionType type) { this.type = type; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Lead getLead() { return lead; }
    public void setLead(Lead lead) { this.lead = lead; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}