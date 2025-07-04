// src/main/java/com/smartlead/repository/LeadRepository.java
package com.smartlead.repository;

import com.smartlead.entity.Lead;
import com.smartlead.entity.Lead.LeadStatus;
import com.smartlead.entity.Lead.LeadScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findByTenantId(Long tenantId);
    List<Lead> findByTenantIdAndStatus(Long tenantId, LeadStatus status);
    List<Lead> findByTenantIdAndScore(Long tenantId, LeadScore score);
    List<Lead> findByAssignedToId(Long userId);
    
    @Query("SELECT l FROM Lead l WHERE l.tenant.id = :tenantId AND l.lastContactedAt < :date")
    List<Lead> findStaleLeads(@Param("tenantId") Long tenantId, @Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.tenant.id = :tenantId AND l.status = :status")
    Long countByTenantIdAndStatus(@Param("tenantId") Long tenantId, @Param("status") LeadStatus status);
}