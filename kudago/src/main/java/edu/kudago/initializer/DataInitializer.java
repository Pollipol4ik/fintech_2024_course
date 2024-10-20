package edu.kudago.initializer;

import edu.kudago.client.ApiClient;
import edu.kudago.dto.Category;
import edu.kudago.dto.Location;
import edu.kudago.service.CategoryService;
import edu.kudago.service.LocationService;
import edu.simplestarter.aspect.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@LogExecutionTime
@Log4j2
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryService categoryService;
    private final LocationService locationService;
    private final ApiClient apiClient;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");

        initializeCategories();
        initializeLocations();

        log.info("Data initialization completed.");
    }

    private void initializeCategories() {
        log.info("Initializing categories...");
        try {
            Category[] categories = apiClient.fetchCategories();
            Optional.ofNullable(categories)
                    .ifPresentOrElse(
                            cats -> {
                                for (Category category : cats) {
                                    categoryService.createCategory(category);
                                }
                                log.info("Categories initialized: {} items", cats.length);
                            },
                            () -> log.warn("No categories found")
                    );
        } catch (Exception e) {
            log.error("Error initializing categories", e);
        }
    }

    private void initializeLocations() {
        log.info("Initializing locations...");
        try {
            Location[] locations = apiClient.fetchLocations(); // вынесено в ApiClient
            Optional.ofNullable(locations)
                    .ifPresentOrElse(
                            locs -> {
                                for (Location location : locs) {
                                    locationService.createLocation(location);
                                }
                                log.info("Locations initialized: {} items", locs.length);
                            },
                            () -> log.warn("No locations found")
                    );
        } catch (Exception e) {
            log.error("Error initializing locations", e);
        }
    }
}
