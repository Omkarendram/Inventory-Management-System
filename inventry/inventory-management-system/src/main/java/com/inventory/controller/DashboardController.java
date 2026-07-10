package com.inventory.controller;

import com.inventory.service.EmailService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {

    private final EmailService emailService;

    public DashboardController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities()
            .stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin ? "redirect:/admin/dashboard" : "redirect:/user/dashboard";
    }

    @GetMapping("/test-email")
    @ResponseBody
    public String testEmail() {
        emailService.sendEmail(
            "rishu09431@gmail.com",
            "Test Email from Inventory System",
            "Email notification is working successfully."
        );

        return "Email sent successfully!";
    }
}
