package edu.kudago.service;

import edu.kudago.dto.Category;
import edu.kudago.exceptions.ResourceNotFoundException;
import edu.kudago.memento.CategoryMemento;
import edu.kudago.memento.HistoryService;
import edu.kudago.storage.InMemoryStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final InMemoryStorage<Category, Integer> storage = new InMemoryStorage<>();
    private final HistoryService<Category, CategoryMemento> historyService;

    public Iterable<Category> getAllCategories() {
        return storage.findAll();
    }

    public Category getCategoryById(Integer id) {
        return storage.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public Category createCategory(Category category) {
        Category createdCategory = storage.save(category.id(), category);
        historyService.saveMemento(createdCategory);
        return createdCategory;
    }

    public Category updateCategory(Integer id, Category category) {
        if (!storage.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        Category updatedCategory = storage.save(id, category);
        historyService.saveMemento(updatedCategory);
        return updatedCategory;
    }

    public void deleteCategory(Integer id) {
        Category category = storage.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        historyService.saveMemento(category);
        storage.deleteById(id);
    }

    public CategoryMemento getLastCategorySnapshot() {
        return historyService.getLastMemento();
    }

    public CategoryMemento getPreviousCategorySnapshot() {
        return historyService.getPreviousMemento();
    }

    public List<CategoryMemento> getHistoryOfSnapshots() {
        return historyService.getHistory();
    }
}
