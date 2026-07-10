package com.inventory.controller;

import com.inventory.entity.Product;
import com.inventory.service.ProductService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class ProductController {

    private static final String UPLOAD_DIR = "C:/inventory-images/";

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/inventory")
    public String inventory(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String category,
        Model model
    ) {
        model.addAttribute("products", getFilteredProducts(name, category));
        model.addAttribute("name", name);
        model.addAttribute("category", category);
        return "inventory";
    }

    @GetMapping("/products")
    public String listProducts(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String category,
        Model model
    ) {
        model.addAttribute("products", getFilteredProducts(name, category));
        model.addAttribute("name", name);
        model.addAttribute("category", category);
        return "products";
    }

    @GetMapping("/products/new")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "add-product";
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public String lowStock(Model model) {
        model.addAttribute("products", productService.getLowStockProducts());
        return "low-stock";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "edit-product";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
        model.addAttribute("products", productService.searchProducts(keyword));
        model.addAttribute("keyword", keyword);
        return "products";
    }

    @PostMapping("/products")
    public String addProduct(
        @ModelAttribute Product product,
        @RequestParam("imageFile") MultipartFile file
    ) throws Exception {

        if (!file.isEmpty()) {
            String fileName = new File(file.getOriginalFilename()).getName();
            Path uploadPath = Path.of(UPLOAD_DIR);
            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            product.setImage(fileName);
        }

        productService.createProduct(product);
        return "redirect:/products";
    }

    @PostMapping("/update-product")
    public String updateProduct(Product product) {
        productService.updateProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProductByGet(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    @GetMapping("/increase-stock/{id}")
    public String increaseStock(@PathVariable Long id) {
        productService.increaseStock(id);
        return "redirect:/low-stock";
    }

    @GetMapping("/decrease-stock/{id}")
    public String decreaseStock(@PathVariable Long id) {
        productService.decreaseStock(id);
        return "redirect:/low-stock";
    }

    @PostMapping("/update-stock")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public String updateStock(
        @RequestParam Long id,
        @RequestParam int quantity
    ) {
        productService.updateStock(id, quantity);
        return "redirect:/low-stock";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    @GetMapping("/export")
    public void exportProducts(HttpServletResponse response) throws Exception {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=products.xlsx";
        response.setHeader(headerKey, headerValue);

        List<Product> products = productService.getAllProducts();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Category");
            headerRow.createCell(3).setCellValue("Price");
            headerRow.createCell(4).setCellValue("Quantity");
            headerRow.createCell(5).setCellValue("Image");

            int rowCount = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowCount++);
                row.createCell(0).setCellValue(product.getId() == null ? 0 : product.getId());
                row.createCell(1).setCellValue(product.getName() == null ? "" : product.getName());
                row.createCell(2).setCellValue(product.getCategory() == null ? "" : product.getCategory());
                row.createCell(3).setCellValue(product.getPrice());
                row.createCell(4).setCellValue(product.getQuantity());
                row.createCell(5).setCellValue(product.getImage() == null ? "" : product.getImage());
            }

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        }
    }

    @GetMapping("/export-low-stock")
    public void exportLowStock(HttpServletResponse response) throws Exception {
        List<Product> products = productService.getLowStockProducts();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Low Stock");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Name");
        header.createCell(2).setCellValue("Category");
        header.createCell(3).setCellValue("Quantity");
        header.createCell(4).setCellValue("Minimum Stock");
        header.createCell(5).setCellValue("Status");

        int rowCount = 1;

        for (Product p : products) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(p.getId() == null ? 0 : p.getId());
            row.createCell(1).setCellValue(p.getName() == null ? "" : p.getName());
            row.createCell(2).setCellValue(p.getCategory() == null ? "" : p.getCategory());
            row.createCell(3).setCellValue(p.getQuantity());
            row.createCell(4).setCellValue(p.getMinimumStock());
            row.createCell(5).setCellValue(p.getQuantity() > p.getMinimumStock() ? "In Stock" : "Low Stock");
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=low_stock.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private List<Product> getFilteredProducts(String name, String category) {
        if (name != null && !name.isEmpty()) {
            return productService.searchProducts(name);
        }

        if (category != null && !category.isEmpty()) {
            return productService.getProductsByCategoryIgnoreCase(category);
        }

        return productService.getAllProducts();
    }
}
