package edu.kudago.command;

import edu.kudago.client.ApiClient;
import edu.kudago.dto.Category;
import edu.kudago.observer.Observer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class InitializeCategoriesCommand implements Command {
    private final ApiClient apiClient;
    private final List<Observer<Category>> observers;

    @Override
    public void execute() {
        Category[] categories = apiClient.fetchCategories();
        if (categories != null) {
            log.info("Fetched {} categories from API", categories.length);
            observers.forEach(observer -> observer.update(Arrays.asList(categories)));
        } else {
            log.warn("No categories fetched from API");
        }
    }
}
