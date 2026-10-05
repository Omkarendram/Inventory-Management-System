package com.inventory.repository;

import com.inventory.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("""
        SELECT MONTH(s.saleDate), SUM(s.totalPrice)
        FROM Sale s
        GROUP BY MONTH(s.saleDate)
        ORDER BY MONTH(s.saleDate)
    """)
    List<Object[]> getMonthlySales();

    @Query("""
        SELECT s.productName, SUM(s.quantity)
        FROM Sale s
        GROUP BY s.productName
        ORDER BY SUM(s.quantity) DESC
    """)
    List<Object[]> getTopSellingProducts();

    List<Sale> findByProductNameContainingIgnoreCase(String productName);

    List<Sale> findTop5ByOrderBySaleDateDescIdDesc();
}
