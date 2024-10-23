package edu.kudago.service;

import edu.cbr.dto.ConvertRequest;
import edu.cbr.dto.ConvertResponse;
import edu.cbr.service.CurrencyService;
import edu.kudago.client.ApiClient;
import edu.kudago.dto.Event;
import edu.kudago.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final ApiClient apiClient;
    private final CurrencyService currencyService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String formatEpochSecond(long epochSecond) {
        return LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now())).format(FORMATTER);
    }

    private void logDateRange(long startDateTimestamp, long endDateTimestamp) {
        log.info("Getting events between {} and {}", formatEpochSecond(startDateTimestamp), formatEpochSecond(endDateTimestamp));
    }

    public CompletableFuture<List<Event>> getEventsByBudget(double budget, String currency, LocalDate dateFrom, LocalDate dateTo) {
        validateDateRange(dateFrom, dateTo);
        LocalDate startDate = (dateFrom != null) ? dateFrom : LocalDate.now();
        LocalDate endDate = (dateTo != null) ? dateTo : startDate.plusDays(6);

        CompletableFuture<Double> convertedBudgetFuture = CompletableFuture.supplyAsync(() -> convertCurrency(budget, currency))
                .exceptionally(ex -> {
                    log.error("Error converting currency: {}", ex.getMessage());
                    return budget;
                });

        CompletableFuture<List<Event>> eventsFuture = CompletableFuture.supplyAsync(() -> {
            long startDateTimestamp = toEpochSecond(startDate);
            long endDateTimestamp = toEpochSecond(endDate);
            logDateRange(startDateTimestamp, endDateTimestamp);
            return apiClient.getEventsBetweenDatesSync(startDateTimestamp, endDateTimestamp);
        }).exceptionally(ex -> {
            log.error("Error fetching events: {}", ex.getMessage());
            return List.of();
        });

        return eventsFuture.thenCombine(convertedBudgetFuture, (events, convertedBudget) -> {
            if (events.isEmpty()) {
                log.warn("No events found for the given budget and date range.");
                throw new ResourceNotFoundException("No events found for the given budget and date range.");
            }

            List<Event> filteredEvents = events.stream()
                    .filter(event -> isValidPrice(event.price(), convertedBudget, event.isFree()))
                    .collect(Collectors.toList());

            if (filteredEvents.isEmpty()) {
                log.warn("No events fit within the given budget.");
                throw new ResourceNotFoundException("No events fit within the given budget.");
            }

            return filteredEvents;
        }).exceptionally(ex -> {
            log.error("Error while processing the request: {}", ex.getMessage(), ex);
            throw new RuntimeException(ex);
        });
    }

    public Mono<List<Event>> getEventsByBudgetAsync(double budget, String currency, LocalDate dateFrom, LocalDate dateTo) {
        validateDateRange(dateFrom, dateTo);
        LocalDate startDate = (dateFrom != null) ? dateFrom : LocalDate.now();
        LocalDate endDate = (dateTo != null) ? dateTo : startDate.plusDays(6);

        long startDateTimestamp = toEpochSecond(startDate);
        long endDateTimestamp = toEpochSecond(endDate);
        logDateRange(startDateTimestamp, endDateTimestamp);

        Mono<List<Event>> eventsMono = apiClient.getEventsBetweenDates(startDateTimestamp, endDateTimestamp).collectList();
        Mono<Double> convertedBudgetMono = Mono.fromCallable(() -> {
            ConvertRequest request = new ConvertRequest(currency, "RUB", BigDecimal.valueOf(budget));
            ConvertResponse response = currencyService.convertCurrency(request);
            double convertedAmount = response.convertedAmount().doubleValue();
            log.info("Converted {} {} to {} RUB. Converted amount: {}", budget, currency, "RUB", convertedAmount);
            return convertedAmount;
        });

        return Mono.zip(eventsMono, convertedBudgetMono)
                .flatMap(tuple -> {
                    List<Event> events = tuple.getT1();
                    double convertedBudget = tuple.getT2();

                    if (events.isEmpty()) {
                        log.warn("No events found for the given budget and date range.");
                        return Mono.error(new ResourceNotFoundException("No events found for the given budget and date range."));
                    }

                    List<Event> filteredEvents = events.stream()
                            .filter(event -> isValidPrice(event.price(), convertedBudget, event.isFree()))
                            .collect(Collectors.toList());

                    if (filteredEvents.isEmpty()) {
                        log.warn("No events fit within the given budget.");
                        return Mono.error(new ResourceNotFoundException("No events fit within the given budget."));
                    }

                    return Mono.just(filteredEvents);
                })
                .onErrorResume(ex -> {
                    log.error("Error while processing the request: {}", ex.getMessage(), ex);
                    return Mono.error(new RuntimeException(ex));
                });
    }

    private void validateDateRange(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new IllegalArgumentException("dateFrom cannot be after dateTo");
        }
    }

    public boolean isValidPrice(String price, double budget, boolean isFree) {
        if (isFree) {
            log.info("Event is free, accepting.");
            return true;
        }

        if (price == null || price.isEmpty()) {
            log.info("Price is missing, assuming the event is free.");
            return true;
        }

        try {
            double extractedPrice = extractFirstPrice(price);
            boolean withinBudget = extractedPrice <= budget;
            log.info("Extracted price: {}. Budget: {}. Within budget: {}", extractedPrice, budget, withinBudget);
            return withinBudget;
        } catch (NumberFormatException e) {
            log.error("Invalid price format: {}", price);
            return false;
        }
    }

    private double extractFirstPrice(String price) {
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(price);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group());
        } else {
            throw new NumberFormatException("No valid price found in: " + price);
        }
    }

    private double convertCurrency(double budget, String currency) {
        ConvertRequest request = new ConvertRequest(currency, "RUB", BigDecimal.valueOf(budget));
        ConvertResponse response = currencyService.convertCurrency(request);
        double convertedAmount = response.convertedAmount().doubleValue();
        log.info("Converted {} {} to {} RUB. Converted amount: {}", budget, currency, "RUB", convertedAmount);
        return convertedAmount;
    }

    private long toEpochSecond(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }
}
