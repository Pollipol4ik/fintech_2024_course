package edu.kudago.îbserver;

import edu.kudago.dto.Category;
import edu.kudago.memento.CategoryHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryObserver implements Observer<Category> {
    private final CategoryHistoryService historyService;

    @Override
    public void update(List<Category> categories) {
        categories.forEach(historyService::saveMemento);
    }
}
