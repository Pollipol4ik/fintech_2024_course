package edu.kudago.service;

import edu.kudago.dto.Category;
import edu.kudago.exceptions.ResourceNotFoundException;
import edu.kudago.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;


public class CategoryServiceTest {

    private CategoryService categoryService;
    private InMemoryStorage<Category, Integer> storage;

    @BeforeEach
    public void setUp() {
        storage = Mockito.mock(InMemoryStorage.class);
        categoryService = new CategoryService(storage);
    }

    @Test
    public void testGetAllCategories() {
        // Arrange
        Category category1 = new Category(1, "category-1", "Category 1");
        Category category2 = new Category(2, "category-2", "Category 2");
        when(storage.findAll()).thenReturn(List.of(category1, category2));

        // Act
        Iterable<Category> categories = categoryService.getAllCategories();

        // Assert
        assertEquals(2, ((Collection<?>) categories).size());
    }

    @Test
    public void testGetCategoryById() {
        // Arrange
        Category category = new Category(1, "category-1", "Category 1");
        when(storage.findById(1)).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.getCategoryById(1);

        // Assert
        assertEquals(category, result);
    }

    @Test
    public void testGetCategoryById_NotFound() {
        // Arrange
        when(storage.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1));
        assertEquals("Category not found with id: 1", exception.getMessage());
    }

    @Test
    public void testCreateCategory() {
        // Arrange
        Category category = new Category(1, "category-1", "Category 1");
        when(storage.save(eq(1), any())).thenReturn(category);

        // Act
        Category result = categoryService.createCategory(category);

        // Assert
        assertEquals(category, result);
        verify(storage, times(1)).save(eq(1), any());
    }

    @Test
    public void testUpdateCategory() {
        // Arrange
        Category category = new Category(1, "category-1", "Category 1");
        when(storage.existsById(1)).thenReturn(true);
        when(storage.save(eq(1), any())).thenReturn(category);

        // Act
        Category result = categoryService.updateCategory(1, category);

        // Assert
        assertEquals(category, result);
        verify(storage, times(1)).save(eq(1), any());
    }

    @Test
    public void testUpdateCategory_NotFound() {
        // Arrange
        Category category = new Category(1, "category-1", "Category 1");
        when(storage.existsById(1)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(1, category));
        assertEquals("Category not found with id: 1", exception.getMessage());
    }

    @Test
    public void testDeleteCategory() {
        // Arrange
        when(storage.existsById(1)).thenReturn(true);

        // Act
        categoryService.deleteCategory(1);

        // Assert
        verify(storage, times(1)).deleteById(1);
    }

    @Test
    public void testDeleteCategory_NotFound() {
        // Arrange
        when(storage.existsById(1)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1));
        assertEquals("Category not found with id: 1", exception.getMessage());
    }
}
