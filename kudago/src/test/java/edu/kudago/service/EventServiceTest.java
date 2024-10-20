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

}
