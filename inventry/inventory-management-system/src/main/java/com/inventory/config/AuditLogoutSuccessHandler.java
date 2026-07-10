package com.inventory.config;

import com.inventory.service.UserSessionAuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuditLogoutSuccessHandler implements LogoutSuccessHandler {

    private final UserSessionAuditService userSessionAuditService;

    public AuditLogoutSuccessHandler(UserSessionAuditService userSessionAuditService) {
        this.userSessionAuditService = userSessionAuditService;
    }

    @Override
    public void onLogoutSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        if (request.getSession(false) != null) {
            userSessionAuditService.logLogout(request.getSession(false).getId());
        }
        response.sendRedirect("/login?logout");
    }
}
