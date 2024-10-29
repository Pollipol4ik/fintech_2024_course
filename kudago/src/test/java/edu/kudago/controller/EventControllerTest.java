package edu.kudago.controller;

import edu.kudago.dto.Event;
import edu.kudago.dto.EventRequestDto;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
        Mockito.when(eventService.getEventsByBudgetAsync(anyDouble(), anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Mono.just(events));

        EventRequestDto requestDto = new EventRequestDto(100.0, "USD", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 10));

        webTestClient.post()
                .uri("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Event.class)
                .hasSize(2)
                .contains(event1, event2);
    }


    @Test
    void testGetEventsByBudgetReactor_InvalidBudget_ShouldReturnBadRequest() {
        EventRequestDto requestDto = new EventRequestDto(Double.NaN, "USD", LocalDate.now(), LocalDate.now().plusDays(1));

        webTestClient.post()
                .uri("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetEventsByBudgetReactor_MissingCurrency_ShouldReturnBadRequest() {
        EventRequestDto requestDto = new EventRequestDto(100.0, null, LocalDate.now(), LocalDate.now().plusDays(1));

        webTestClient.post()
                .uri("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetEventsByBudgetReactor_MissingBudget_ShouldReturnBadRequest() {
        EventRequestDto requestDto = new EventRequestDto(null, "USD", LocalDate.now(), LocalDate.now().plusDays(1));

        webTestClient.post()
                .uri("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetEventsByBudgetReactor_EmptyRequest_ShouldReturnBadRequest() {
        webTestClient.post()
                .uri("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isBadRequest();
    }


    @Test
    void testGetEventsByBudgetReactor_ValidParamsWithFutureDates() {
        List<Event> events = List.of(event1, event2);
        Mockito.when(eventService.getEventsByBudgetAsync(anyDouble(), anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Mono.just(events));

        EventRequestDto requestDto = new EventRequestDto(100.0, "USD", LocalDate.now().plusDays(1), LocalDate.now().plusDays(10));

        webTestClient.post()
                .uri("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Event.class)
                .hasSize(2)
                .contains(event1, event2);
    }
}
