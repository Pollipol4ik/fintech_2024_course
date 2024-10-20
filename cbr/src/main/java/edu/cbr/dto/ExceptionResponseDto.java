package edu.cbr.dto;

import jakarta.validation.constraints.NotNull;

public record ExceptionResponseDto(@NotNull
                                        boolean success,
                                   String message) {
}
