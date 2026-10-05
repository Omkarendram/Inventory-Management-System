package com.inventory.config;

import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.entity.Supplier;
import com.inventory.repository.CategoryRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.SupplierRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CatalogDataInitializer {

    @Bean
    public CommandLineRunner seedCatalog(
        CategoryRepository categoryRepository,
        ProductRepository productRepository,
        SupplierRepository supplierRepository
    ) {
        return args -> {
            if (categoryRepository.count() == 0) {
                categoryRepository.saveAll(List.of(
                    new Category(null, "Electronics", "Computers, displays, and gadgets"),
                    new Category(null, "Office Furniture", "Ergonomic chairs, standing desks, and lamps"),
                    new Category(null, "Computer Peripherals", "Keyboards, mice, and docking stations"),
                    new Category(null, "Stationery", "Notebooks, pens, and paper supplies")
                ));
            }

            if (supplierRepository.count() == 0) {
                Supplier s1 = new Supplier();
                s1.setName("TechDistro Global");
                s1.setEmail("sales@techdistro.com");
                s1.setPhone("+1-555-0199");
                s1.setAddress("100 Innovation Way, San Jose, CA");

                Supplier s2 = new Supplier();
                s2.setName("Ergonomic Solutions Ltd");
                s2.setEmail("orders@ergosolutions.com");
                s2.setPhone("+1-555-0248");
                s2.setAddress("450 Comfort Blvd, Grand Rapids, MI");

                Supplier s3 = new Supplier();
                s3.setName("Apex Office Supplies");
                s3.setEmail("contact@apexsupplies.com");
                s3.setPhone("+1-555-0371");
                s3.setAddress("78 Commerce Park, Chicago, IL");

                supplierRepository.saveAll(List.of(s1, s2, s3));
            }

            if (productRepository.count() == 0) {
                productRepository.saveAll(List.of(
                    new Product(null, "ThinkPad X1 Carbon Gen 11", "Electronics", 1450.00, 15, 5, null),
                    new Product(null, "Logitech MX Master 3S Mouse", "Computer Peripherals", 99.99, 42, 10, null),
                    new Product(null, "Dell UltraSharp 27\" 4K Monitor", "Electronics", 520.00, 8, 3, null),
                    new Product(null, "Herman Miller Aeron Chair", "Office Furniture", 1200.00, 4, 2, null),
                    new Product(null, "Keychron K2 Wireless Keyboard", "Computer Peripherals", 85.00, 2, 5, null),
                    new Product(null, "Moleskine Classic Ruled Notebook", "Stationery", 19.50, 120, 25, null),
                    new Product(null, "Anker 100W USB-C GaN Charger", "Electronics", 49.99, 1, 10, null)
                ));
            }
        };
    }
}
