package com.inventory.scheduler;

import com.inventory.entity.Product;
import com.inventory.service.EmailService;
import com.inventory.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class LowStockReportScheduler {

    private static final Logger logger = LoggerFactory.getLogger(LowStockReportScheduler.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ProductService productService;
    private final EmailService emailService;
    private final String recipientEmail;
    private final boolean schedulingEnabled;

    public LowStockReportScheduler(
        ProductService productService,
        EmailService emailService,
        @Value("${app.notification.low-stock-recipient:admin@inventory.local}") String recipientEmail,
        @Value("${app.scheduling.low-stock-report.enabled:true}") boolean schedulingEnabled
    ) {
        this.productService = productService;
        this.emailService = emailService;
        this.recipientEmail = recipientEmail;
        this.schedulingEnabled = schedulingEnabled;
    }

    /**
     * Scheduled job running everyday at 10:00 AM (0 0 10 * * ?)
     * Cron expression can be configured via app.scheduling.low-stock-report.cron
     */
    @Scheduled(cron = "${app.scheduling.low-stock-report.cron:0 0 10 * * ?}")
    public void executeScheduledDailyReport() {
        if (!schedulingEnabled) {
            logger.info("Daily low-stock email report is disabled by configuration.");
            return;
        }

        logger.info("Triggering scheduled daily low-stock email report at 10:00 AM...");
        sendDailyLowStockReport();
    }

    /**
     * Generates and transmits the low-stock email report.
     * Can be invoked by the scheduler or triggered on-demand by administrators.
     *
     * @return count of low-stock items included in the report
     */
    public int sendDailyLowStockReport() {
        if (recipientEmail == null || recipientEmail.isBlank()) {
            logger.warn("Daily low-stock report aborted: No recipient email configured.");
            return 0;
        }

        List<Product> lowStockProducts = productService.getLowStockProducts();
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String subject = String.format("[Daily IMS Alert] Low Stock Summary Report - %s (%d items)",
            LocalDateTime.now().toLocalDate(), lowStockProducts.size());

        StringBuilder body = new StringBuilder();
        body.append("========================================================================\n");
        body.append("              INVENTORY MANAGEMENT SYSTEM - DAILY LOW-STOCK REPORT      \n");
        body.append("========================================================================\n");
        body.append("Report Generated : ").append(timestamp).append("\n");
        body.append("Recipient        : ").append(recipientEmail).append("\n");
        body.append("Total Items Flagged: ").append(lowStockProducts.size()).append("\n\n");

        if (lowStockProducts.isEmpty()) {
            body.append("STATUS: HEALTHY\n");
            body.append("All inventory items currently exceed their minimum stock threshold.\n");
            body.append("No immediate restocking actions are required.\n");
        } else {
            body.append("STATUS: ATTENTION REQUIRED\n");
            body.append("The following items have reached or fallen below the minimum stock threshold:\n\n");
            body.append(String.format("%-5s | %-32s | %-20s | %-7s | %-5s | %-7s\n",
                "ID", "Product Name", "Category", "Stock", "Min", "Deficit"));
            body.append("----------------------------------------------------------------------------------------\n");

            for (Product p : lowStockProducts) {
                int stock = p.getQuantity();
                int min = p.getMinimumStock();
                int deficit = min - stock;
                String deficitStr = deficit > 0 ? ("-" + deficit) : "0";

                body.append(String.format("%-5d | %-32s | %-20s | %-7d | %-5d | %-7s\n",
                    p.getId(),
                    truncate(p.getName(), 32),
                    truncate(p.getCategory() != null ? p.getCategory() : "N/A", 20),
                    stock,
                    min,
                    deficitStr
                ));
            }
            body.append("----------------------------------------------------------------------------------------\n\n");
            body.append("Action Recommended: Please initiate purchase orders or restock requests promptly.\n");
            body.append("Access the IMS portal: http://localhost:9090/low-stock\n");
        }

        body.append("\n========================================================================\n");
        body.append("This is an automated system report. Please do not reply directly to this email.\n");

        emailService.sendEmail(recipientEmail, subject, body.toString());
        logger.info("Successfully dispatched daily low-stock email report to {} with {} item(s).",
            recipientEmail, lowStockProducts.size());

        return lowStockProducts.size();
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public boolean isSchedulingEnabled() {
        return schedulingEnabled;
    }

    private String truncate(String val, int maxLen) {
        if (val == null) return "";
        if (val.length() <= maxLen) return val;
        return val.substring(0, maxLen - 3) + "...";
    }
}
