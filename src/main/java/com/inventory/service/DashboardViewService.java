package com.inventory.service;

import com.inventory.entity.Product;
import com.inventory.entity.Supplier;
import com.inventory.entity.Category;
import com.inventory.repository.CategoryRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.SaleRepository;
import com.inventory.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardViewService {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final SaleRepository saleRepository;

    public DashboardViewService(
        ProductService productService,
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        SupplierRepository supplierRepository,
        SaleRepository saleRepository
    ) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.saleRepository = saleRepository;
    }

    public void populateDashboard(Model model) {
        populateDashboard(model, null);
    }

    public void populateDashboard(Model model, String keyword) {
        List<Product> products = productService.getAllProducts();
        List<Product> lowStockProducts = productService.getLowStockProducts();
        int lowStockCount = lowStockProducts.size();
        double inventoryValue = products.stream()
            .mapToDouble(product -> product.getPrice() * product.getQuantity())
            .sum();

        model.addAttribute("products", products);
        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalCategories", categoryRepository.count());
        model.addAttribute("totalSuppliers", supplierRepository.count());
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("inventoryValue", inventoryValue);

        List<Object[]> categoryData = productRepository.getTopCategories();
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        for (Object[] row : categoryData) {
            String category = (String) row[0];
            Number count = (Number) row[1];

            if (category == null || category.isBlank()) {
                continue;
            }

            labels.add(category);
            values.add(count != null ? count.intValue() : 0);
        }

        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartValues", values);

        List<Object[]> monthlyResults = saleRepository.getMonthlySales();
        int[] monthlySales = new int[12];
        for (Object[] row : monthlyResults) {
            int month = ((Number) row[0]).intValue();
            int total = ((Number) row[1]).intValue();
            monthlySales[month - 1] = total;
        }
        model.addAttribute("monthlySales", monthlySales);

        List<String> lowStockLabels = new ArrayList<>();
        List<Integer> lowStockValues = new ArrayList<>();
        for (int i = 0; i < Math.min(5, lowStockProducts.size()); i++) {
            lowStockLabels.add(lowStockProducts.get(i).getName());
            lowStockValues.add(lowStockProducts.get(i).getQuantity());
        }
        model.addAttribute("lowStockLabels", lowStockLabels);
        model.addAttribute("lowStockValues", lowStockValues);

        List<Object[]> topProductData = saleRepository.getTopSellingProducts();
        List<String> topProductLabels = new ArrayList<>();
        List<Integer> topProductValues = new ArrayList<>();
        for (int i = 0; i < Math.min(5, topProductData.size()); i++) {
            topProductLabels.add((String) topProductData.get(i)[0]);
            topProductValues.add(((Number) topProductData.get(i)[1]).intValue());
        }
        model.addAttribute("topProductLabels", topProductLabels);
        model.addAttribute("topProductValues", topProductValues);
        model.addAttribute("recentSales", saleRepository.findTop5ByOrderBySaleDateDescIdDesc());
        addGlobalSearchResults(model, keyword);

        if (lowStockCount > 0) {
            model.addAttribute("lowStockMessage", "Warning! " + lowStockCount + " products are low in stock.");
        }
    }

    private void addGlobalSearchResults(Model model, String keyword) {
        model.addAttribute("searchKeyword", keyword);

        if (keyword == null || keyword.isBlank()) {
            model.addAttribute("productSearchResults", List.of());
            model.addAttribute("categorySearchResults", List.of());
            model.addAttribute("supplierSearchResults", List.of());
            model.addAttribute("globalSearchHasResults", false);
            return;
        }

        String searchTerm = keyword.trim();
        List<Product> productResults = productRepository.findByNameContainingIgnoreCase(searchTerm);
        List<Category> categoryResults = categoryRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm);
        List<Supplier> supplierResults = supplierRepository
            .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrAddressContainingIgnoreCase(
                searchTerm,
                searchTerm,
                searchTerm,
                searchTerm
            );

        model.addAttribute("productSearchResults", productResults);
        model.addAttribute("categorySearchResults", categoryResults);
        model.addAttribute("supplierSearchResults", supplierResults);
        model.addAttribute("globalSearchHasResults", !productResults.isEmpty() || !categoryResults.isEmpty() || !supplierResults.isEmpty());
    }
}
