package com.inventory.repository;

import com.inventory.entity.UserSessionAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSessionAuditRepository extends JpaRepository<UserSessionAudit, Long> {
    List<UserSessionAudit> findAllByOrderByLoginTimeDescIdDesc();

    Optional<UserSessionAudit> findFirstBySessionIdAndLogoutTimeIsNullOrderByLoginTimeDesc(String sessionId);
}
