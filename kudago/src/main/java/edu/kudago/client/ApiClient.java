package edu.kudago.client;

import edu.kudago.dto.Category;
import edu.kudago.dto.Event;
import edu.kudago.dto.EventApiResponse;
import edu.kudago.dto.Location;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.Semaphore;

@Component
@RequiredArgsConstructor
@Log4j2
public class ApiClient {

    private final WebClient webClient;

    @Value("${api.max-concurrent-requests}")
    private int maxConcurrentRequests;

    private Semaphore semaphore;

    @PostConstruct
    public void initSemaphore() {
        this.semaphore = new Semaphore(maxConcurrentRequests, true);
    }

    public Flux<Category> fetchCategories() {
        return Flux.defer(() -> {
            try {
                semaphore.acquire();
                log.info("KudaGo API request to fetch categories started");
                return webClient
                        .get()
                        .uri("/place-categories/")
                        .retrieve()
                        .bodyToFlux(Category.class)
                        .doFinally(signalType -> {
                            semaphore.release();
                            log.info("KudaGo API request to fetch categories completed");
                        });
            } catch (InterruptedException e) {
                log.error("Error acquiring semaphore: {}", e.getMessage());
                return Flux.empty();
            }
        });
    }

    public Flux<Location> fetchLocations() {
        return Flux.defer(() -> {
            try {
                semaphore.acquire();
                log.info("KudaGo API request to fetch locations started");
                return webClient
                        .get()
                        .uri("/locations/")
                        .retrieve()
                        .bodyToFlux(Location.class)
                        .doFinally(signalType -> {
                            semaphore.release(); // Release semaphore slot
                            log.info("KudaGo API request to fetch locations completed");
                        });
            } catch (InterruptedException e) {
                log.error("Error acquiring semaphore: {}", e.getMessage());
                return Flux.empty();
            }
        });
    }

    public Flux<Event> getEventsBetweenDates(long startDateTimestamp, long endDateTimestamp) {
        return Flux.defer(() -> {
            try {
                semaphore.acquire();
                log.info("KudaGo API request to fetch events started");
                return webClient
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/events/")
                                .queryParam("actual_since", startDateTimestamp)
                                .queryParam("actual_until", endDateTimestamp)
                                .queryParam("fields", "id,title,price")
                                .build())
                        .retrieve()
                        .bodyToMono(EventApiResponse.class)
                        .flatMapMany(response -> response != null ? Flux.fromIterable(response.events()) : Flux.empty())
                        .doFinally(signalType -> {
                            semaphore.release();
                            log.info("KudaGo API request to fetch events completed");
                        });
            } catch (InterruptedException e) {
                log.error("Error acquiring semaphore: {}", e.getMessage());
                return Flux.empty();
            }
        });
    }

    public List<Event> getEventsBetweenDatesSync(long startDateTimestamp, long endDateTimestamp) {
        try {
            semaphore.acquire();
            log.info("Synchronous KudaGo API request to fetch events started. Semaphore permits available: {}", semaphore.availablePermits());
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/events/")
                            .queryParam("actual_since", startDateTimestamp)
                            .queryParam("actual_until", endDateTimestamp)
                            .queryParam("fields", "id,title,price")
                            .build())
                    .retrieve()
                    .bodyToMono(EventApiResponse.class)
                    .map(EventApiResponse::events)
                    .blockOptional()
                    .orElse(List.of());
        } catch (InterruptedException e) {
            log.error("Error acquiring semaphore: {}", e.getMessage());
            return List.of();
        } finally {
            semaphore.release();
            log.info("Synchronous KudaGo API request to fetch events completed. Semaphore permits available: {}", semaphore.availablePermits());
        }
    }
    public int getAvailablePermits() {
        return semaphore.availablePermits();
    }

}

