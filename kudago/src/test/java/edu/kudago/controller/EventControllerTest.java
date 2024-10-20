package edu.kudago.controller;

import edu.kudago.dto.Event;
import edu.kudago.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;

@WebFluxTest(EventController.class)
public class EventControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private EventService eventService;

    private Event event1;
    private Event event2;

    @BeforeEach
    void setUp() {
        event1 = new Event(1L, "Concert", "50", false);
        event2 = new Event(2L, "Exhibition", "30", false);
    }

    @Test
    void testGetEventsByBudgetReactor_Success() {
        List<Event> events = List.of(event1, event2);
        Mockito.when(eventService.getEventsByBudgetAsync(anyDouble(), anyString(), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class)))
                .thenReturn(Mono.just(events));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/events")
                        .queryParam("budget", 100.0)
                        .queryParam("currency", "USD")
                        .queryParam("dateFrom", "2024-01-01")
                        .queryParam("dateTo", "2024-01-10")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Event.class)
                .hasSize(2)
                .contains(event1, event2);
    }

    @Test
    void testGetEventsByBudgetReactor_NoEventsFound() {
        Mockito.when(eventService.getEventsByBudgetAsync(anyDouble(), anyString(), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class)))
                .thenReturn(Mono.just(Collections.emptyList()));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/events")
                        .queryParam("budget", 100.0)
                        .queryParam("currency", "USD")
                        .queryParam("dateFrom", "2024-01-01")
                        .queryParam("dateTo", "2024-01-10")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testGetEventsByBudgetReactor_InvalidBudget_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/events")
                        .queryParam("budget", "invalid")
                        .queryParam("currency", "USD")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetEventsByBudgetReactor_MissingCurrency_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/events")
                        .queryParam("budget", 100.0)
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetEventsByBudgetReactor_MissingBudget_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/events")
                        .queryParam("currency", "USD")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetEventsByBudgetReactor_EmptyQueryParams_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/events")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetEventsByBudgetReactor_InvalidDateFormat_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/events")
                        .queryParam("budget", 100.0)
                        .queryParam("currency", "USD")
                        .queryParam("dateFrom", "invalid-date")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetEventsByBudgetReactor_ValidParamsWithFutureDates() {
        List<Event> events = List.of(event1, event2);
        Mockito.when(eventService.getEventsByBudgetAsync(anyDouble(), anyString(), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class)))
                .thenReturn(Mono.just(events));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/events")
                        .queryParam("budget", 100.0)
                        .queryParam("currency", "USD")
                        .queryParam("dateFrom", LocalDate.now().plusDays(1).toString())
                        .queryParam("dateTo", LocalDate.now().plusDays(10).toString())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Event.class)
                .hasSize(2)
                .contains(event1, event2);
    }
}
