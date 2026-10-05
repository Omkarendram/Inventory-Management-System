package com.inventory.service;

import com.inventory.entity.User;
import com.inventory.entity.UserSessionAudit;
import com.inventory.repository.UserSessionAuditRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserSessionAuditService {

    private final UserSessionAuditRepository userSessionAuditRepository;
    private final UserService userService;

    public UserSessionAuditService(UserSessionAuditRepository userSessionAuditRepository, UserService userService) {
        this.userSessionAuditRepository = userSessionAuditRepository;
        this.userService = userService;
    }

    public void logLogin(String userId, String sessionId) {
        User user = userService.findByUserId(userId);

        UserSessionAudit audit = new UserSessionAudit();
        audit.setUserId(userId);
        audit.setUsername(user != null ? user.getUsername() : userId);
        audit.setRole(user != null ? user.getRole() : "");
        audit.setSessionId(sessionId);
        audit.setLoginTime(LocalDateTime.now());
        userSessionAuditRepository.save(audit);
    }

    public void logLogout(String sessionId) {
        userSessionAuditRepository.findFirstBySessionIdAndLogoutTimeIsNullOrderByLoginTimeDesc(sessionId)
            .ifPresent(audit -> {
                audit.setLogoutTime(LocalDateTime.now());
                audit.updateDuration();
                userSessionAuditRepository.save(audit);
            });
    }

    public List<UserSessionAudit> getAllAuditLogs() {
        return userSessionAuditRepository.findAllByOrderByLoginTimeDescIdDesc();
    }
}
