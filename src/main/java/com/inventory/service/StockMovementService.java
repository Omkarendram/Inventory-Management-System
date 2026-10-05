package com.inventory.service;

import com.inventory.entity.Product;
import com.inventory.entity.StockMovement;
import com.inventory.entity.StockMovementType;
import com.inventory.repository.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;

    public StockMovementService(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    public List<StockMovement> getAllMovements() {
        return stockMovementRepository.findAllByOrderByMovementDateDescIdDesc();
    }

    public void recordMovement(Product product, StockMovementType movementType, int quantity) {
        if (product == null || quantity <= 0) {
            return;
        }

        StockMovement movement = new StockMovement();
        movement.setProductId(product.getId());
        movement.setProductName(product.getName());
        movement.setMovementType(movementType);
        movement.setQuantity(quantity);
        movement.setUnitPrice(product.getPrice());
        movement.setTotalValue(product.getPrice() * quantity);
        movement.setMovementDate(LocalDate.now());
        stockMovementRepository.save(movement);
    }
}
