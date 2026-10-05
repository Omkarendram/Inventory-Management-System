package com.inventory.controller;

import com.inventory.entity.User;
import com.inventory.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserManagementController {

    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        if (!model.containsAttribute("userForm")) {
            model.addAttribute("userForm", new User());
        }
        return "users";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute("userForm") User user, RedirectAttributes redirectAttributes) {
        if (user.getUserId() == null || user.getUserId().isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User ID is required.");
            redirectAttributes.addFlashAttribute("userForm", user);
            return "redirect:/users";
        }

        if (userService.userIdExists(user.getUserId().trim())) {
            redirectAttributes.addFlashAttribute("errorMessage", "User ID already exists.");
            redirectAttributes.addFlashAttribute("userForm", user);
            return "redirect:/users";
        }

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Name is required.");
            redirectAttributes.addFlashAttribute("userForm", user);
            return "redirect:/users";
        }

        if (userService.usernameExists(user.getUsername().trim())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Name already exists.");
            redirectAttributes.addFlashAttribute("userForm", user);
            return "redirect:/users";
        }

        user.setUserId(user.getUserId().trim());
        user.setUsername(user.getUsername() == null ? "" : user.getUsername().trim());
        user.setRole(user.getRole() == null || user.getRole().isBlank() ? "USER" : user.getRole());
        user.setPhone(user.getPhone() == null ? "" : user.getPhone().trim());
        user.setEmail(user.getEmail() == null ? "" : user.getEmail().trim());
        user.setAddress(user.getAddress() == null ? "" : user.getAddress().trim());
        userService.saveUser(user);

        redirectAttributes.addFlashAttribute("successMessage", "User created successfully.");
        return "redirect:/users";
    }

    @GetMapping("/users/{id}")
    public String userProfile(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        if (!model.containsAttribute("userProfile")) {
            User user = userService.getUserById(id);
            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
                return "redirect:/users";
            }
            model.addAttribute("userProfile", user);
        }
        return "user-profile";
    }

    @PostMapping("/users/{id}")
    public String updateUserProfile(
        @PathVariable Long id,
        @ModelAttribute("userProfile") User user,
        @RequestParam(required = false) String newPassword,
        RedirectAttributes redirectAttributes
    ) {
        user.setId(id);
        user.setUserId(user.getUserId() == null ? "" : user.getUserId().trim());
        user.setUsername(user.getUsername() == null ? "" : user.getUsername().trim());
        user.setPhone(user.getPhone() == null ? "" : user.getPhone().trim());
        user.setEmail(user.getEmail() == null ? "" : user.getEmail().trim());
        user.setAddress(user.getAddress() == null ? "" : user.getAddress().trim());

        if (user.getUserId().isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User ID is required.");
            redirectAttributes.addFlashAttribute("userProfile", user);
            return "redirect:/users/" + id;
        }

        if (user.getUsername().isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Name is required.");
            redirectAttributes.addFlashAttribute("userProfile", user);
            return "redirect:/users/" + id;
        }

        if (userService.userIdExistsForAnotherUser(user.getUserId(), id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "User ID already exists.");
            redirectAttributes.addFlashAttribute("userProfile", user);
            return "redirect:/users/" + id;
        }

        if (userService.usernameExistsForAnotherUser(user.getUsername(), id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Name already exists.");
            redirectAttributes.addFlashAttribute("userProfile", user);
            return "redirect:/users/" + id;
        }

        userService.updateUserProfile(user, newPassword);
        redirectAttributes.addFlashAttribute("successMessage", "User profile updated successfully.");
        return "redirect:/users/" + id;
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        return "redirect:/users";
    }
}
