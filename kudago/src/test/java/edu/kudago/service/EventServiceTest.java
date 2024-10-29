package edu.kudago.service;

import edu.kudago.client.ApiClient;
import edu.kudago.dto.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class EventServiceTest {

    @Mock
    private ApiClient apiClient;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testGetEventsByBudgetSync() {
        Event event1 = new Event(1L, "Event 1", "50", false);
        Event event2 = new Event(2L, "Event 2", "100", false);

        when(apiClient.getEventsBetweenDatesSync(anyLong(), anyLong()))
                .thenReturn(List.of(event1, event2));

        CompletableFuture<List<Event>> future = eventService.getEventsByBudget(90.0, "RUB", LocalDate.now(), null);
        List<Event> result = future.join();

        System.out.println("Filtered events: " + result);

        assertEquals(1, result.size(), "Filtered event count does not match expected");
        assertEquals("Event 1", result.get(0).title());
        verify(apiClient, times(1)).getEventsBetweenDatesSync(anyLong(), anyLong());
    }

    @Test
    void testGetEventsByBudgetAsync() {
        Event event1 = new Event(1L, "Event 1", "50", false);
        Event event2 = new Event(2L, "Event 2", "150", false);

        when(apiClient.getEventsBetweenDates(anyLong(), anyLong()))
                .thenReturn(Flux.just(event1, event2));

        Mono<List<Event>> result = eventService.getEventsByBudgetAsync(100.0, "RUB", LocalDate.now(), null);
        List<Event> events = result.block();


        System.out.println("Filtered events: " + events);

        assertEquals(1, events.size(), "Filtered event count does not match expected");
        assertEquals("Event 1", events.get(0).title());
        verify(apiClient, times(1)).getEventsBetweenDates(anyLong(), anyLong());
    }


    @Test
    void testGetEventsByBudget_InvalidDateRange() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 10);
        LocalDate dateTo = LocalDate.of(2024, 1, 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.getEventsByBudget(100.0, "USD", dateFrom, dateTo);
        });

        assertEquals("dateFrom cannot be after dateTo", exception.getMessage());
    }

    @Test
    void testGetEventsByBudget_UnsupportedCurrency() {
        Event event1 = new Event(1L, "Event 1", "50", false);
        when(apiClient.getEventsBetweenDatesSync(anyLong(), anyLong()))
                .thenReturn(List.of(event1));

        CompletableFuture<List<Event>> future = eventService.getEventsByBudget(100.0, "XYZ", LocalDate.now(), null);
        List<Event> result = future.join();

        assertEquals(1, result.size(), "Event should be returned with unsupported currency");
    }

    @Test
    void testGetEventsByBudgetAsync_Error() {
        when(apiClient.getEventsBetweenDates(anyLong(), anyLong()))
                .thenReturn(Flux.error(new RuntimeException("API error")));

        Mono<List<Event>> result = eventService.getEventsByBudgetAsync(100.0, "USD", LocalDate.now(), null);

        RuntimeException exception = assertThrows(RuntimeException.class, result::block);
        assertEquals("API error", exception.getCause().getMessage());
    }

    @Test
    void testGetEventsByBudgetAsync_FilterByBudget() {
        Event event1 = new Event(1L, "Event 1", "50", false);
        Event event2 = new Event(2L, "Event 2", "150", false);
        when(apiClient.getEventsBetweenDates(anyLong(), anyLong()))
                .thenReturn(Flux.just(event1, event2));

        Mono<List<Event>> result = eventService.getEventsByBudgetAsync(100.0, "RUB", LocalDate.now(), null);
        List<Event> events = result.block();

        assertEquals(1, events.size(), "Only one event should fit within the budget");
        assertEquals("Event 1", events.get(0).title());
    }

    @Test
    void testIsValidPrice_FreeEvent() {
        boolean isValid = eventService.isValidPrice("", 100.0, true);
        assertTrue(isValid, "Free events should always be valid.");
    }

    @Test
    void testIsValidPrice_InvalidPriceFormat() {
        boolean isValid = eventService.isValidPrice("invalid", 100.0, false);
        assertFalse(isValid, "Invalid price format should not be valid.");
    }

    @Test
    void testIsValidPrice_WithinBudget() {
        boolean isValid = eventService.isValidPrice("50", 100.0, false);
        assertTrue(isValid, "Price within budget should be valid.");
    }

    @Test
    void testIsValidPrice_OutsideBudget() {
        boolean isValid = eventService.isValidPrice("150", 100.0, false);
        assertFalse(isValid, "Price outside budget should not be valid.");
    }

}
