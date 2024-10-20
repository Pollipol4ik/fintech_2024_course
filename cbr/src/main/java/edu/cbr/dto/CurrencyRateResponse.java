package edu.cbr.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CurrencyRateResponse(
        @Schema(description = "The currency code", example = "USD")
        String currency,

        @Schema(description = "The exchange rate of the currency", example = "123.4")
        double rate
) {
}
