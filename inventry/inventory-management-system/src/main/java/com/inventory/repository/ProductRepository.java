package com.inventory.repository;

import com.inventory.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    List<Product> findByNameContaining(String name);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategory(String category);
    List<Product> findByCategoryIgnoreCase(String category);

    @Query(value = """
        SELECT category, COUNT(*) as total
        FROM products
        GROUP BY category
        ORDER BY total DESC
        LIMIT 6
    """, nativeQuery = true)
    List<Object[]> getTopCategories();

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Product> findByQuantityLessThan(int quantity);

    long countByQuantityLessThanEqual(int quantity);

    @Override
    Page<Product> findAll(Pageable pageable);
}
