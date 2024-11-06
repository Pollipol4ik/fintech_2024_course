package edu.kudago.configuration;

import edu.kudago.dto.Category;
import edu.kudago.dto.Location;
import edu.kudago.memento.CategoryMemento;
import edu.kudago.memento.HistoryService;
import edu.kudago.memento.LocationMemento;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HistoryConfig {

    @Bean
    public HistoryService<Category, CategoryMemento> categoryHistoryService() {
        return new HistoryService<>(category ->
                new CategoryMemento(category.id(), category.name(), category.slug())
        );
    }

    @Bean
    public HistoryService<Location, LocationMemento> locationHistoryService() {
        return new HistoryService<>(location ->
                new LocationMemento(location.slug(), location.name())
        );
    }
}
