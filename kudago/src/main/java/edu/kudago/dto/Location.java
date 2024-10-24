package edu.kudago.dto;

import jakarta.validation.constraints.NotBlank;

public record Location(
        @NotBlank(message = "Slug cannot be blank") String slug,
        @NotBlank(message = "Name cannot be blank") String name) {
}
