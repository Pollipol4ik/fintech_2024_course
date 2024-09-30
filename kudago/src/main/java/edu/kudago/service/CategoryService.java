package edu.kudago.service;

import edu.kudago.dto.Category;
import edu.kudago.exceptions.ResourceNotFoundException;
import edu.kudago.storage.InMemoryStorage;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final InMemoryStorage<Category, Integer> storage = new InMemoryStorage<>();

    public Iterable<Category> getAllCategories() {
        return storage.findAll();
    }

    public Category getCategoryById(Integer id) {
        return storage.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public Category createCategory(Category category) {
        return storage.save(category.id(), category);
    }

    public Category updateCategory(Integer id, Category category) {
        if (!storage.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        return storage.save(id, category);
    }

    public void deleteCategory(Integer id) {
        if (!storage.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        storage.deleteById(id);
    }
}
