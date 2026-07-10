package com.inventory.config;

import com.inventory.service.UserSessionAuditService;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

@Component
public class AuditSessionListener implements HttpSessionListener {

    private final UserSessionAuditService userSessionAuditService;

    public AuditSessionListener(UserSessionAuditService userSessionAuditService) {
        this.userSessionAuditService = userSessionAuditService;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        userSessionAuditService.logLogout(se.getSession().getId());
    }
}
