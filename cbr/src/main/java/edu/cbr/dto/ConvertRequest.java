package edu.cbr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ConvertRequest(
        @Schema(description = "The currency code from which the conversion is made", example = "USD")
        @Size(min = 3, max = 3, message = "The currency code must contain exactly 3 characters")
        @NotBlank(message = "The currency code for conversion cannot be empty")
        String fromCurrency,

        @Schema(description = "The currency code to which the conversion is made", example = "RUB")
        @Size(min = 3, max = 3, message = "The currency code must contain exactly 3 characters")
        @NotBlank(message = "The target currency code cannot be empty")
        String toCurrency,

        @Positive(message = "The amount for conversion must be a positive number")
        BigDecimal amount
) {
}
