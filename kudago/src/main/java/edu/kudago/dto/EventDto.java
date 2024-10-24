package edu.kudago.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record EventDto(
        @NotBlank(message = "Name cannot be blank") String name,
        @NotNull(message = "Date cannot be null") LocalDateTime date,
        @NotNull(message = "Place ID cannot be null") Long placeId) {
}
