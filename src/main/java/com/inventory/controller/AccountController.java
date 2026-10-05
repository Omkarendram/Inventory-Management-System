package com.inventory.controller;

import com.inventory.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
        Authentication authentication,
        @RequestParam String currentPassword,
        @RequestParam String newPassword,
        @RequestParam String confirmPassword,
        RedirectAttributes redirectAttributes
    ) {
        if (newPassword == null || newPassword.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "New password is required.");
            return "redirect:/change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New password and confirm password do not match.");
            return "redirect:/change-password";
        }

        boolean changed = userService.changePassword(authentication.getName(), currentPassword, newPassword);
        if (!changed) {
            redirectAttributes.addFlashAttribute("errorMessage", "Current password is incorrect.");
            return "redirect:/change-password";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");
        return "redirect:/change-password";
    }
}
