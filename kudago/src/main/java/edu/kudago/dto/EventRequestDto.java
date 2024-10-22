package edu.kudago.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EventRequestDto(@NotNull(message = "Budget is required")
                              @Min(value = 0, message = "Budget must be greater than or equal to 0")
                              Double budget,
                              @NotBlank(message = "Currency is required")
                              String currency,
                              LocalDate dateFrom,
                              LocalDate dateTo) {
}
