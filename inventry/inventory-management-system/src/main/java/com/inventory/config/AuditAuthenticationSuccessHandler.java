package com.inventory.config;

import com.inventory.service.UserSessionAuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuditAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserSessionAuditService userSessionAuditService;

    public AuditAuthenticationSuccessHandler(UserSessionAuditService userSessionAuditService) {
        this.userSessionAuditService = userSessionAuditService;
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        String sessionId = request.getSession().getId();
        String userId = authentication.getName();

        request.getSession().setAttribute("auditSessionId", sessionId);
        userSessionAuditService.logLogin(userId, sessionId);

        boolean isAdmin = authentication.getAuthorities()
            .stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        response.sendRedirect(isAdmin ? "/admin/dashboard" : "/user/dashboard");
    }
}
