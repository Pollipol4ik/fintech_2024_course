package edu.kudago.service;

import edu.kudago.dto.Category;
import edu.kudago.storage.InMemoryStorage;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CategoryService {
    private final InMemoryStorage<Category, Integer> storage = new InMemoryStorage<>();

    public Iterable<Category> getAllCategories() {
        return storage.findAll();
    }

    public Optional<Category> getCategoryById(Integer id) {
        return storage.findById(id);
    }

    public Category createCategory(Category category) {
        return storage.save(category.id(), category);
    }

    public Category updateCategory(Integer id, Category category) {
        return storage.save(id, category);
    }

    public void deleteCategory(Integer id) {
        storage.deleteById(id);
    }
}
