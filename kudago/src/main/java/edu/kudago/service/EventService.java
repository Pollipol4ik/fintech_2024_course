package edu.kudago.service;

import edu.kudago.client.ApiClient;
import edu.kudago.dto.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String formatEpochSecond(long epochSecond) {
        return LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now())).format(formatter);
    }

    private void logDateRange(long startDateTimestamp, long endDateTimestamp) {
        log.info("Getting events between {} and {}", formatEpochSecond(startDateTimestamp), formatEpochSecond(endDateTimestamp));
    }

    public CompletableFuture<List<Event>> getEventsByBudget(double budget, String currency, LocalDate dateFrom, LocalDate dateTo) {
        validateDateRange(dateFrom, dateTo);
        LocalDate startDate = (dateFrom != null) ? dateFrom : LocalDate.now();
        LocalDate endDate = (dateTo != null) ? dateTo : startDate.plusDays(6);

        CompletableFuture<Double> convertedBudgetFuture = CompletableFuture.supplyAsync(() -> convertCurrency(budget, currency));
        CompletableFuture<List<Event>> eventsFuture = CompletableFuture.supplyAsync(() -> {
            long startDateTimestamp = toEpochSecond(startDate);
            long endDateTimestamp = toEpochSecond(endDate);
            logDateRange(startDateTimestamp, endDateTimestamp);
            return apiClient.getEventsBetweenDatesSync(startDateTimestamp, endDateTimestamp);
        });

        return eventsFuture.thenCombine(convertedBudgetFuture, (events, convertedBudget) ->
                events.stream()
                        .filter(event -> isValidPrice(event.price(), convertedBudget, event.isFree()))
                        .collect(Collectors.toList())
        );
    }


    public Mono<List<Event>> getEventsByBudgetAsync(double budget, String currency, LocalDate dateFrom, LocalDate dateTo) {
        validateDateRange(dateFrom, dateTo);
        LocalDate startDate = (dateFrom != null) ? dateFrom : LocalDate.now();
        LocalDate endDate = (dateTo != null) ? dateTo : startDate.plusDays(6);

        long startDateTimestamp = toEpochSecond(startDate);
        long endDateTimestamp = toEpochSecond(endDate);
        logDateRange(startDateTimestamp, endDateTimestamp);

        return Mono.zip(
                apiClient.getEventsBetweenDates(startDateTimestamp, endDateTimestamp).collectList(),
                convertCurrencyAsync(budget, currency)
        ).map(tuple -> {
            List<Event> events = tuple.getT1();
            double convertedBudget = tuple.getT2();
            return events.stream()
                    .filter(event -> isValidPrice(event.price(), convertedBudget, event.isFree()))
                    .collect(Collectors.toList());
        });
    }


    private boolean isValidPrice(String price, double budget, boolean isFree) {
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
        return switch (currency.toUpperCase()) {
            case "USD" -> budget * 96.0;
            case "EUR" -> budget * 105.0;
            default -> budget;
        };
    }

    private void validateDateRange(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new IllegalArgumentException("dateFrom cannot be after dateTo");
        }
    }


    private Mono<Double> convertCurrencyAsync(double budget, String currency) {
        return Mono.fromCallable(() -> convertCurrency(budget, currency));
    }

    private long toEpochSecond(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }
}
