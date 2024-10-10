package edu.cbr.client;

import edu.cbr.entity.ValCurs;
import edu.cbr.entity.Valute;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CbrClient {

    private final RestClient restClient;

    @CircuitBreaker(name = "cbrClient", fallbackMethod = "fallbackDailyRates")
    public List<Valute> getDailyRates() {

        ValCurs valCursResponse = restClient
                .get()
                .uri("/XML_daily.asp/")
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, (response, request) -> {
                    log.error("Error response: {}", response);
                    throw new HttpServerErrorException(HttpStatusCode.valueOf(503));
                })
                .toEntity(ValCurs.class)
                .getBody();
        return Optional.ofNullable(valCursResponse)
                .map(ValCurs::getValutes)
                .orElseThrow(() -> new HttpServerErrorException(HttpStatusCode.valueOf(503)));
    }

    public List<Valute> fallbackDailyRates(Throwable throwable) {
        log.warn("Fallback method triggered: Unable to reach CBR service due to: {}", throwable.getMessage());
        return Collections.emptyList();
    }
}
