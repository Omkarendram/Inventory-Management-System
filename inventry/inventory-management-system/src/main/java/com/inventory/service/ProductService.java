package com.inventory.service;

import com.inventory.entity.Product;
import com.inventory.entity.StockMovementType;
import com.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final EmailService emailService;
    private final StockMovementService stockMovementService;
    private final String lowStockRecipient;

    public ProductService(
        ProductRepository productRepository,
        EmailService emailService,
        StockMovementService stockMovementService,
        @Value("${app.notification.low-stock-recipient:}") String lowStockRecipient
    ) {
        this.productRepository = productRepository;
        this.emailService = emailService;
        this.stockMovementService = stockMovementService;
        this.lowStockRecipient = lowStockRecipient;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public long getProductCount() {
        return productRepository.count();
    }

    public Page<Product> getProductsPage(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        if (keyword == null || keyword.isBlank()) {
            return productRepository.findAll(pageable);
        }
        return productRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
    }

    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product saveProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        checkLowStock(savedProduct);
        return savedProduct;
    }

    public Product createProduct(Product product) {
        Product savedProduct = saveProduct(product);
        stockMovementService.recordMovement(savedProduct, StockMovementType.ADDED, savedProduct.getQuantity());
        return savedProduct;
    }

    public Product updateProduct(Product product) {
        Product existingProduct = productRepository.findById(product.getId()).orElseThrow();
        int previousQuantity = existingProduct.getQuantity();

        existingProduct.setName(product.getName());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setMinimumStock(product.getMinimumStock());
        existingProduct.setImage(product.getImage());

        Product savedProduct = saveProduct(existingProduct);
        recordQuantityChange(savedProduct, previousQuantity, savedProduct.getQuantity());
        return savedProduct;
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getProductsByCategoryIgnoreCase(String category) {
        return productRepository.findByCategoryIgnoreCase(category);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Product updateStock(Long id, int quantity) {
        Product product = productRepository.findById(id).orElseThrow();
        int previousQuantity = product.getQuantity();
        product.setQuantity(quantity);
        Product savedProduct = saveProduct(product);
        recordQuantityChange(savedProduct, previousQuantity, quantity);
        return savedProduct;
    }

    public Product increaseStock(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        int previousQuantity = product.getQuantity();
        product.setQuantity(product.getQuantity() + 1);
        Product savedProduct = saveProduct(product);
        recordQuantityChange(savedProduct, previousQuantity, savedProduct.getQuantity());
        return savedProduct;
    }

    public Product decreaseStock(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        if (product.getQuantity() > 0) {
            int previousQuantity = product.getQuantity();
            product.setQuantity(product.getQuantity() - 1);
            Product savedProduct = saveProduct(product);
            recordQuantityChange(savedProduct, previousQuantity, savedProduct.getQuantity());
            return savedProduct;
        }
        return product;
    }

    private void recordQuantityChange(Product product, int previousQuantity, int newQuantity) {
        int difference = newQuantity - previousQuantity;

        if (difference > 0) {
            stockMovementService.recordMovement(product, StockMovementType.ADDED, difference);
        } else if (difference < 0) {
            stockMovementService.recordMovement(product, StockMovementType.REDUCED, Math.abs(difference));
        }
    }

    public long getLowStockCount(int threshold) {
        return productRepository.countByQuantityLessThanEqual(threshold);
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findAll().stream()
            .filter(product -> product.getQuantity() <= product.getMinimumStock())
            .sorted(Comparator.comparing(Product::getId, Comparator.nullsLast(Long::compareTo)))
            .collect(Collectors.toList());
    }

    public void checkLowStock(Product product) {
        if (lowStockRecipient == null || lowStockRecipient.isBlank()) {
            return;
        }

        if (product.getQuantity() <= product.getMinimumStock()) {
            emailService.sendEmail(
                lowStockRecipient,
                "Low Stock Alert",
                "Product: " + product.getName()
                    + "\nRemaining Stock: " + product.getQuantity()
                    + "\nMinimum Stock: " + product.getMinimumStock()
                    + "\nPlease restock soon."
            );
        }
    }
}
