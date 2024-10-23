package edu.kudago.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record EventApiResponse(@JsonProperty("results")
                               List<Event> events) {
}
