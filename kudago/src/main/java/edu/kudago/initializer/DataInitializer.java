package edu.kudago.initializer;

import edu.kudago.aspect.LogExecutionTime;
import edu.kudago.dto.Category;
import edu.kudago.dto.Location;
import edu.kudago.service.CategoryService;
import edu.kudago.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;

@Component
@LogExecutionTime
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final CategoryService categoryService;
    private final LocationService locationService;
    private final WebClient webClient;

    @Autowired
    public DataInitializer(CategoryService categoryService, LocationService locationService, WebClient.Builder webClientBuilder) {
        this.categoryService = categoryService;
        this.locationService = locationService;
        this.webClient = webClientBuilder.baseUrl("https://kudago.com/public-api/v1.4").build();
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data initialization...");

        initializeCategories();
        initializeLocations();

        logger.info("Data initialization completed.");
    }

    private void initializeCategories() {
        logger.info("Initializing categories...");

        Category[] categories = webClient.get()
                .uri("/place-categories/")
                .retrieve()
                .bodyToMono(Category[].class)
                .block();

        if (categories != null) {
            for (Category category : categories) {
                categoryService.createCategory(category);
            }
            logger.info("Categories initialized: {} items", categories.length);
        } else {
            logger.warn("Failed to fetch categories from API");
        }
    }

    private void initializeLocations() {
        logger.info("Initializing locations...");

        Location[] locations = webClient.get()
                .uri("/locations/")
                .retrieve()
                .bodyToMono(Location[].class)
                .block();

        if (locations != null) {
            for (Location location : locations) {
                locationService.createLocation(location);
            }
            logger.info("Locations initialized: {} items", locations.length);
        } else {
            logger.warn("Failed to fetch locations from API");
        }
    }
}
