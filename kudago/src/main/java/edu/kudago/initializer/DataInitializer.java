package edu.kudago.initializer;

import edu.kudago.client.ApiClient;
import edu.kudago.service.CategoryService;
import edu.kudago.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Log4j2
public class DataInitializer {

    private final ApiClient apiClient;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final ExecutorService fixedThreadPool;
    private final ScheduledExecutorService scheduledThreadPool;

    @Value("${app.initializationInterval}")
    private Duration initializationInterval;

    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStarted() {
        log.info("Application has started.");
        try {
            scheduledThreadPool.scheduleAtFixedRate(this::initializeData, 0, initializationInterval.toMillis(), TimeUnit.MILLISECONDS);
            log.info("Scheduled data initialization every {} ms", initializationInterval.toMillis());
        } catch (Exception e) {
            log.error("Error scheduling data initialization", e);
        }
    }

    public void initializeData() {
        long startTime = System.currentTimeMillis();
        log.info("Starting data initialization...");

        Future<?> categoryInitFuture = fixedThreadPool.submit(this::initializeCategories);
        Future<?> locationInitFuture = fixedThreadPool.submit(this::initializeLocations);

        try {
            categoryInitFuture.get();
            locationInitFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error during data initialization", e);
            Thread.currentThread().interrupt();
        }

        long endTime = System.currentTimeMillis();
        log.info("Data initialization completed in {} ms", (endTime - startTime));
    }


    private void initializeCategories() {
        log.info("Initializing categories...");

        apiClient.fetchCategories()
                .flatMap(category -> Mono.just(categoryService.createCategory(category)))
                .doOnComplete(() -> log.info("Categories initialization completed"))
                .doOnError(e -> log.error("Error initializing categories", e))
                .count()
                .doOnNext(count -> log.info("Categories initialized: {} items", count))
                .toFuture()
                .join();
    }

    private void initializeLocations() {
        log.info("Initializing locations...");
        apiClient.fetchLocations()
                .flatMap(location -> Mono.just(locationService.createLocation(location)))
                .doOnComplete(() -> log.info("Locations initialization completed"))
                .doOnError(e -> log.error("Error initializing locations", e))
                .count()
                .doOnNext(count -> log.info("Locations initialized: {} items", count))
                .toFuture()
                .join();
    }


}
