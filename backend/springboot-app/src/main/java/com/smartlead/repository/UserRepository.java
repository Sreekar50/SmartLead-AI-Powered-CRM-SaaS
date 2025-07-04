// src/main/java/com/smartlead/repository/UserRepository.java
package com.smartlead.repository;

import com.smartlead.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByTenantId(Long tenantId);
    Optional<User> findByEmailAndTenantId(String email, Long tenantId);
}