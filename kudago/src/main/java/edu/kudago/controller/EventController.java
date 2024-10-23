package edu.kudago.controller;

import edu.kudago.dto.Event;
import edu.kudago.dto.EventRequestDto;
import edu.kudago.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
@Slf4j
@Validated
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Get events by budget")
    @PostMapping
    public Mono<ResponseEntity<List<Event>>> getEventsByBudgetReactor(@Validated @RequestBody EventRequestDto eventRequest) {
        log.info("Received request (Reactor) to get events with budget: {}, currency: {}, dateFrom: {}, dateTo: {}",
                eventRequest.budget(), eventRequest.currency(), eventRequest.dateFrom(), eventRequest.dateTo());

        return eventService.getEventsByBudgetAsync(eventRequest.budget(), eventRequest.currency(),
                        eventRequest.dateFrom(), eventRequest.dateTo())
                .map(ResponseEntity::ok);
    }
}
