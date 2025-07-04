// src/main/java/com/smartlead/service/TenantService.java
package com.smartlead.service;

import com.smartlead.entity.Tenant;
import com.smartlead.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Optional;

@Service
public class TenantService {
    
    @Autowired
    private TenantRepository tenantRepository;
    
    public Tenant createTenant(String companyName, String domain) {
        String tenantId = UUID.randomUUID().toString();
        Tenant tenant = new Tenant(tenantId, companyName, domain);
        return tenantRepository.save(tenant);
    }
    
    public Optional<Tenant> findByTenantId(String tenantId) {
        return tenantRepository.findByTenantId(tenantId);
    }
    
    public Optional<Tenant> findByDomain(String domain) {
        return tenantRepository.findByDomain(domain);
    }
}