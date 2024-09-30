package edu.kudago.client;

import edu.kudago.dto.Category;
import edu.kudago.dto.Location;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Log4j2
public class ApiClient {

    private final WebClient webClient;

    public Category[] fetchCategories() {
        try {
            return webClient.get()
                    .uri("/place-categories/")
                    .retrieve()
                    .bodyToMono(Category[].class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error fetching categories from API: {}", e.getMessage());
            return null;
        }
    }

    public Location[] fetchLocations() {
        try {
            return webClient.get()
                    .uri("/locations/")
                    .retrieve()
                    .bodyToMono(Location[].class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error fetching locations from API: {}", e.getMessage());
            return null;
        }
    }
}
