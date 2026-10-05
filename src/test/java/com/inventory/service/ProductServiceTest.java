package com.inventory.service;

import com.inventory.entity.Product;
import com.inventory.entity.StockMovementType;
import com.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private StockMovementService stockMovementService;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(
            productRepository,
            emailService,
            stockMovementService,
            "admin@example.com"
        );
    }

    @Test
    void testGetAllProducts() {
        Product p1 = new Product(1L, "Laptop", "Electronics", 1200.0, 10, 2, "laptop.png");
        Product p2 = new Product(2L, "Mouse", "Electronics", 25.0, 50, 10, "mouse.png");
        when(productRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Product> products = productService.getAllProducts();
        assertEquals(2, products.size());
        assertEquals("Laptop", products.get(0).getName());
    }

    @Test
    void testGetProductById() {
        Product p = new Product(1L, "Monitor", "Electronics", 300.0, 15, 3, "monitor.png");
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        Product found = productService.getProductById(1L);
        assertNotNull(found);
        assertEquals("Monitor", found.getName());
    }

    @Test
    void testCreateProductRecordsStockMovement() {
        Product product = new Product(null, "Keyboard", "Peripherals", 75.0, 20, 5, "kb.png");
        Product saved = new Product(1L, "Keyboard", "Peripherals", 75.0, 20, 5, "kb.png");

        when(productRepository.save(product)).thenReturn(saved);

        Product result = productService.createProduct(product);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(stockMovementService, times(1)).recordMovement(saved, StockMovementType.ADDED, 20);
    }

    @Test
    void testIncreaseStock() {
        Product product = new Product(1L, "Desk", "Furniture", 200.0, 5, 2, "desk.png");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product updated = productService.increaseStock(1L);
        assertEquals(6, updated.getQuantity());
        verify(stockMovementService, times(1)).recordMovement(updated, StockMovementType.ADDED, 1);
    }

    @Test
    void testDecreaseStock() {
        Product product = new Product(1L, "Desk", "Furniture", 200.0, 5, 2, "desk.png");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product updated = productService.decreaseStock(1L);
        assertEquals(4, updated.getQuantity());
        verify(stockMovementService, times(1)).recordMovement(updated, StockMovementType.REDUCED, 1);
    }

    @Test
    void testCheckLowStockSendsAlertWhenAtOrBelowThreshold() {
        Product product = new Product(1L, "Paper", "Stationery", 5.0, 2, 5, "paper.png");
        productService.checkLowStock(product);

        verify(emailService, times(1)).sendEmail(
            eq("admin@example.com"),
            eq("Low Stock Alert"),
            contains("Paper")
        );
    }

    @Test
    void testCheckLowStockDoesNotSendAlertWhenAboveThreshold() {
        Product product = new Product(1L, "Paper", "Stationery", 5.0, 10, 5, "paper.png");
        productService.checkLowStock(product);

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testGetLowStockProducts() {
        Product p1 = new Product(1L, "Pen", "Stationery", 1.0, 2, 5, null); // low stock
        Product p2 = new Product(2L, "Notebook", "Stationery", 3.0, 20, 5, null); // normal
        when(productRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Product> lowStockList = productService.getLowStockProducts();
        assertEquals(1, lowStockList.size());
        assertEquals("Pen", lowStockList.get(0).getName());
    }
}
