// src/main/java/com/smartlead/repository/TenantRepository.java
package com.smartlead.repository;

import com.smartlead.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByTenantId(String tenantId);
    Optional<Tenant> findByDomain(String domain);
}