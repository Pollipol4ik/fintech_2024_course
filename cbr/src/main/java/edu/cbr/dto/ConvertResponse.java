package edu.cbr.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ConvertResponse(
        @Schema(description = "The currency being converted from", example = "USD")
        String fromCurrency,

        @Schema(description = "The target currency", example = "RUB")
        String toCurrency,

        @Schema(description = "The converted amount", example = "9000.5")
        Double convertedAmount
) {
}
