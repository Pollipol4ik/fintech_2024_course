package edu.kudago.observer;

import edu.kudago.dto.Category;
import edu.kudago.memento.CategoryMemento;
import edu.kudago.memento.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryObserver implements Observer<Category> {
    private final HistoryService<Category, CategoryMemento> historyService;

    @Override
    public void update(List<Category> categories) {
        categories.forEach(historyService::saveMemento);
    }
}
