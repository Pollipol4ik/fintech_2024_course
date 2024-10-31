package edu.kudago.memento;

import edu.kudago.dto.Category;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Service
public class CategoryHistoryService {
    private final List<CategoryMemento> history = new ArrayList<>();

    public void saveMemento(Category category) {
        history.add(new CategoryMemento(category.id(), category.name(), category.slug()));
    }

}