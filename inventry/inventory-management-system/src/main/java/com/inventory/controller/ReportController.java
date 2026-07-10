package com.inventory.controller;

import com.inventory.entity.Product;
import com.inventory.entity.Sale;
import com.inventory.entity.StockMovement;
import com.inventory.entity.UserSessionAudit;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.SaleRepository;
import com.inventory.service.StockMovementService;
import com.inventory.service.UserSessionAuditService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ReportController {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final StockMovementService stockMovementService;
    private final UserSessionAuditService userSessionAuditService;

    public ReportController(
        ProductRepository productRepository,
        SaleRepository saleRepository,
        StockMovementService stockMovementService,
        UserSessionAuditService userSessionAuditService
    ) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.stockMovementService = stockMovementService;
        this.userSessionAuditService = userSessionAuditService;
    }

    @GetMapping("/reports")
    public String reports(@RequestParam(required = false) String product, Model model) {
        List<Sale> sales;
        if (product != null && !product.isBlank()) {
            sales = saleRepository.findByProductNameContainingIgnoreCase(product);
            model.addAttribute("product", product);
        } else {
            sales = saleRepository.findAll();
        }

        model.addAttribute("sales", sales);
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("stockMovements", stockMovementService.getAllMovements());
        model.addAttribute("userAuditLogs", userSessionAuditService.getAllAuditLogs());
        return "reports";
    }

    @GetMapping("/sales")
    public String sales(@RequestParam(required = false) String product, Model model) {
        return reports(product, model);
    }

    @GetMapping("/export-sales")
    public void exportSales(HttpServletResponse response) throws Exception {
        List<Sale> sales = saleRepository.findAll();

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=sales_report.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sales Report");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Product");
            header.createCell(2).setCellValue("Quantity");
            header.createCell(3).setCellValue("Total Price");
            header.createCell(4).setCellValue("Sale Date");

            int rowNum = 1;
            for (Sale sale : sales) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(sale.getId() == null ? 0 : sale.getId());
                row.createCell(1).setCellValue(sale.getProductName() == null ? "" : sale.getProductName());
                row.createCell(2).setCellValue(sale.getQuantity());
                row.createCell(3).setCellValue(sale.getTotalPrice());
                row.createCell(4).setCellValue(sale.getSaleDate() == null ? "" : sale.getSaleDate().toString());
            }

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    @GetMapping("/export-stock")
    public void exportStock(HttpServletResponse response) throws Exception {
        List<Product> products = productRepository.findAll();

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=stock_report.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Stock Report");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Category");
            header.createCell(3).setCellValue("Quantity");
            header.createCell(4).setCellValue("Minimum Stock");
            header.createCell(5).setCellValue("Status");

            int rowNum = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(product.getId() == null ? 0 : product.getId());
                row.createCell(1).setCellValue(product.getName() == null ? "" : product.getName());
                row.createCell(2).setCellValue(product.getCategory() == null ? "" : product.getCategory());
                row.createCell(3).setCellValue(product.getQuantity());
                row.createCell(4).setCellValue(product.getMinimumStock());
                row.createCell(5).setCellValue(product.getQuantity() > product.getMinimumStock() ? "In Stock" : "Low Stock");
            }

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    @GetMapping("/export-products")
    public void exportProducts(HttpServletResponse response) throws Exception {
        List<Product> products = productRepository.findAll();

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=product_report.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Product Report");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Category");
            header.createCell(3).setCellValue("Price");
            header.createCell(4).setCellValue("Quantity");
            header.createCell(5).setCellValue("Minimum Stock");
            header.createCell(6).setCellValue("Status");

            int rowNum = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(product.getId() == null ? 0 : product.getId());
                row.createCell(1).setCellValue(product.getName() == null ? "" : product.getName());
                row.createCell(2).setCellValue(product.getCategory() == null ? "" : product.getCategory());
                row.createCell(3).setCellValue(product.getPrice());
                row.createCell(4).setCellValue(product.getQuantity());
                row.createCell(5).setCellValue(product.getMinimumStock());
                row.createCell(6).setCellValue(product.getQuantity() > product.getMinimumStock() ? "In Stock" : "Low Stock");
            }

            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    @GetMapping("/export-stock-movements")
    public void exportStockMovements(HttpServletResponse response) throws Exception {
        List<StockMovement> stockMovements = stockMovementService.getAllMovements();

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=stock_history_report.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Stock History");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Product ID");
            header.createCell(2).setCellValue("Product Name");
            header.createCell(3).setCellValue("Movement Type");
            header.createCell(4).setCellValue("Quantity");
            header.createCell(5).setCellValue("Unit Price");
            header.createCell(6).setCellValue("Total Value");
            header.createCell(7).setCellValue("Movement Date");

            int rowNum = 1;
            for (StockMovement movement : stockMovements) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(movement.getId() == null ? 0 : movement.getId());
                row.createCell(1).setCellValue(movement.getProductId() == null ? 0 : movement.getProductId());
                row.createCell(2).setCellValue(movement.getProductName() == null ? "" : movement.getProductName());
                row.createCell(
                    3
                ).setCellValue(
                    movement.getMovementType() == null
                        ? ""
                        : movement.getMovementType().name().equals("ADDED") ? "Stock In" : "Stock Out"
                );
                row.createCell(4).setCellValue(movement.getQuantity());
                row.createCell(5).setCellValue(movement.getUnitPrice());
                row.createCell(6).setCellValue(movement.getTotalValue());
                row.createCell(7).setCellValue(movement.getMovementDate() == null ? "" : movement.getMovementDate().toString());
            }

            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    @GetMapping("/export-user-audit")
    public void exportUserAudit(HttpServletResponse response) throws Exception {
        List<UserSessionAudit> auditLogs = userSessionAuditService.getAllAuditLogs();

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=user_audit_report.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Login Audit");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("User ID");
            header.createCell(2).setCellValue("Name");
            header.createCell(3).setCellValue("Role");
            header.createCell(4).setCellValue("Login Time");
            header.createCell(5).setCellValue("Logout Time");
            header.createCell(6).setCellValue("Duration (Minutes)");

            int rowNum = 1;
            for (UserSessionAudit audit : auditLogs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(audit.getId() == null ? 0 : audit.getId());
                row.createCell(1).setCellValue(audit.getUserId() == null ? "" : audit.getUserId());
                row.createCell(2).setCellValue(audit.getUsername() == null ? "" : audit.getUsername());
                row.createCell(3).setCellValue(audit.getRole() == null ? "" : audit.getRole());
                row.createCell(4).setCellValue(audit.getLoginTime() == null ? "" : audit.getLoginTime().toString());
                row.createCell(5).setCellValue(audit.getLogoutTime() == null ? "" : audit.getLogoutTime().toString());
                row.createCell(6).setCellValue(audit.getDurationMinutes() == null ? 0 : audit.getDurationMinutes());
            }

            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }
}
