package com.inventory.controller;

import com.inventory.scheduler.LowStockReportScheduler;
import com.inventory.service.DashboardViewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DashboardViewService dashboardViewService;
    private final LowStockReportScheduler lowStockReportScheduler;

    public AdminController(
        DashboardViewService dashboardViewService,
        LowStockReportScheduler lowStockReportScheduler
    ) {
        this.dashboardViewService = dashboardViewService;
        this.lowStockReportScheduler = lowStockReportScheduler;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(@RequestParam(required = false) String keyword, Model model) {
        dashboardViewService.populateDashboard(model, keyword);
        return "admin-dashboard";
    }

    @PostMapping("/send-low-stock-report")
    public String triggerLowStockReport(RedirectAttributes redirectAttributes) {
        int count = lowStockReportScheduler.sendDailyLowStockReport();
        redirectAttributes.addFlashAttribute("reportSuccess",
            "Daily low-stock report dispatched to " + lowStockReportScheduler.getRecipientEmail()
                + " (" + count + " item(s) flagged).");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/send-low-stock-report")
    public String triggerLowStockReportGet(RedirectAttributes redirectAttributes) {
        return triggerLowStockReport(redirectAttributes);
    }
}
