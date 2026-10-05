package com.inventory.service;

import com.inventory.entity.Category;
import com.inventory.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    void testGetAllCategories() {
        Category c1 = new Category(1L, "Electronics", "Electronic Devices");
        Category c2 = new Category(2L, "Stationery", "Office Supplies");
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<Category> result = categoryService.getAllCategories();
        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getName());
    }

    @Test
    void testSearchCategories() {
        Category c1 = new Category(1L, "Electronics", "Gadgets and tech");
        when(categoryRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("elec", "elec"))
            .thenReturn(List.of(c1));

        List<Category> result = categoryService.searchCategories("elec");
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getName());
    }

    @Test
    void testSaveCategory() {
        Category cat = new Category(null, "Hardware", "Tools");
        Category saved = new Category(1L, "Hardware", "Tools");
        when(categoryRepository.save(cat)).thenReturn(saved);

        Category result = categoryService.saveCategory(cat);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testDeleteCategory() {
        categoryService.deleteCategory(5L);
        verify(categoryRepository, times(1)).deleteById(5L);
    }
}
