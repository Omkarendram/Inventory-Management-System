package com.inventory.controller;

import com.inventory.entity.Category;
import com.inventory.service.ApprovalRequestService;
import com.inventory.service.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RequestController {

    private final ApprovalRequestService approvalRequestService;
    private final ProductService productService;

    public RequestController(ApprovalRequestService approvalRequestService, ProductService productService) {
        this.approvalRequestService = approvalRequestService;
        this.productService = productService;
    }

    @GetMapping("/requests")
    public String requests(Authentication authentication, Model model) {
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categoryRequest", new Category());

        if (isAdmin) {
            model.addAttribute("requests", approvalRequestService.getAllRequests());
        } else {
            model.addAttribute("requests", approvalRequestService.getRequestsForUser(authentication.getName()));
        }
        return "requests";
    }

    @PostMapping("/requests/stock")
    public String createStockRequest(
        Authentication authentication,
        @RequestParam Long productId,
        @RequestParam int requestedQuantity,
        RedirectAttributes redirectAttributes
    ) {
        approvalRequestService.createStockUpdateRequest(authentication.getName(), productId, requestedQuantity);
        redirectAttributes.addFlashAttribute("successMessage", "Stock update request sent for admin approval.");
        return "redirect:/requests";
    }

    @PostMapping("/requests/category")
    public String createCategoryRequest(
        Authentication authentication,
        @RequestParam String name,
        @RequestParam(required = false) String description,
        RedirectAttributes redirectAttributes
    ) {
        approvalRequestService.createCategoryAddRequest(authentication.getName(), name, description);
        redirectAttributes.addFlashAttribute("successMessage", "Category request sent for admin approval.");
        return "redirect:/requests";
    }

    @PostMapping("/requests/{id}/approve")
    public String approveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        approvalRequestService.approveRequest(id);
        redirectAttributes.addFlashAttribute("successMessage", "Request approved successfully.");
        return "redirect:/requests";
    }

    @PostMapping("/requests/{id}/reject")
    public String rejectRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        approvalRequestService.rejectRequest(id);
        redirectAttributes.addFlashAttribute("successMessage", "Request rejected successfully.");
        return "redirect:/requests";
    }

    @PostMapping("/requests/approve-all")
    public String approveAllRequests(RedirectAttributes redirectAttributes) {
        int processedCount = approvalRequestService.approveAllPendingRequests();
        redirectAttributes.addFlashAttribute(
            "successMessage",
            processedCount > 0
                ? processedCount + " pending request(s) approved successfully."
                : "No pending requests to approve."
        );
        return "redirect:/requests";
    }

    @PostMapping("/requests/reject-all")
    public String rejectAllRequests(RedirectAttributes redirectAttributes) {
        int processedCount = approvalRequestService.rejectAllPendingRequests();
        redirectAttributes.addFlashAttribute(
            "successMessage",
            processedCount > 0
                ? processedCount + " pending request(s) rejected successfully."
                : "No pending requests to reject."
        );
        return "redirect:/requests";
    }
}
