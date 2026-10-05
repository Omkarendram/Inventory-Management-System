package com.inventory.scheduler;

import com.inventory.entity.Product;
import com.inventory.service.EmailService;
import com.inventory.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LowStockReportSchedulerTest {

    @Mock
    private ProductService productService;

    @Mock
    private EmailService emailService;

    private LowStockReportScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new LowStockReportScheduler(
            productService,
            emailService,
            "admin@inventory.local",
            true
        );
    }

    @Test
    void testSendDailyLowStockReportWithFlaggedItems() {
        Product p1 = new Product(5L, "Keychron K2 Wireless Keyboard", "Computer Peripherals", 85.0, 2, 5, null);
        Product p2 = new Product(7L, "Anker 100W USB-C GaN Charger", "Electronics", 49.99, 1, 10, null);

        when(productService.getLowStockProducts()).thenReturn(List.of(p1, p2));

        int reportedCount = scheduler.sendDailyLowStockReport();
        assertEquals(2, reportedCount);

        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService, times(1)).sendEmail(
            eq("admin@inventory.local"),
            subjectCaptor.capture(),
            bodyCaptor.capture()
        );

        String subject = subjectCaptor.getValue();
        assertTrue(subject.contains("[Daily IMS Alert] Low Stock Summary Report"));
        assertTrue(subject.contains("(2 items)"));

        String body = bodyCaptor.getValue();
        assertTrue(body.contains("STATUS: ATTENTION REQUIRED"));
        assertTrue(body.contains("Keychron K2 Wireless Keyboard"));
        assertTrue(body.contains("Anker 100W USB-C GaN Charger"));
        assertTrue(body.contains("Total Items Flagged: 2"));
    }

    @Test
    void testSendDailyLowStockReportWhenStockIsHealthy() {
        when(productService.getLowStockProducts()).thenReturn(Collections.emptyList());

        int reportedCount = scheduler.sendDailyLowStockReport();
        assertEquals(0, reportedCount);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService, times(1)).sendEmail(
            eq("admin@inventory.local"),
            anyString(),
            bodyCaptor.capture()
        );

        String body = bodyCaptor.getValue();
        assertTrue(body.contains("STATUS: HEALTHY"));
        assertTrue(body.contains("All inventory items currently exceed their minimum stock threshold"));
    }

    @Test
    void testSendDailyLowStockReportWithMissingRecipientAborts() {
        LowStockReportScheduler unconfiguredScheduler = new LowStockReportScheduler(
            productService,
            emailService,
            "",
            true
        );

        int count = unconfiguredScheduler.sendDailyLowStockReport();
        assertEquals(0, count);
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testScheduledExecutionWhenDisabled() {
        LowStockReportScheduler disabledScheduler = new LowStockReportScheduler(
            productService,
            emailService,
            "admin@inventory.local",
            false
        );

        disabledScheduler.executeScheduledDailyReport();
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testScheduledExecutionWhenEnabled() {
        when(productService.getLowStockProducts()).thenReturn(Collections.emptyList());

        scheduler.executeScheduledDailyReport();
        verify(emailService, times(1)).sendEmail(eq("admin@inventory.local"), anyString(), anyString());
    }
}
