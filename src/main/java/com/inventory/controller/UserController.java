package com.inventory.controller;

import com.inventory.service.DashboardViewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    private final DashboardViewService dashboardViewService;

    public UserController(DashboardViewService dashboardViewService) {
        this.dashboardViewService = dashboardViewService;
    }

    @GetMapping("/dashboard")
    public String userDashboard(Model model) {
        dashboardViewService.populateDashboard(model);
        return "user-dashboard";
    }
}
