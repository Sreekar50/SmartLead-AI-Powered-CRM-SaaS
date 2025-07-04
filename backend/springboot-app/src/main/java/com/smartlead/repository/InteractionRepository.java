// src/main/java/com/smartlead/repository/InteractionRepository.java
package com.smartlead.repository;

import com.smartlead.entity.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {
    List<Interaction> findByLeadIdOrderByCreatedAtDesc(Long leadId);
    List<Interaction> findByUserIdOrderByCreatedAtDesc(Long userId);
}