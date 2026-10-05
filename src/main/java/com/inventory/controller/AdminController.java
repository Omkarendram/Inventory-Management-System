package com.inventory.controller;

import com.inventory.service.DashboardViewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DashboardViewService dashboardViewService;

    public AdminController(DashboardViewService dashboardViewService) {
        this.dashboardViewService = dashboardViewService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(@RequestParam(required = false) String keyword, Model model) {
        dashboardViewService.populateDashboard(model, keyword);
        return "admin-dashboard";
    }
}
