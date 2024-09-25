package edu.kudago.controller;

import edu.kudago.aspect.LogExecutionTime;
import edu.kudago.dto.Location;
import edu.kudago.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@LogExecutionTime
public class LocationController {

    private final LocationService locationService;

    @Operation(summary = "Get all locations")
    @GetMapping
    public Iterable<Location> getAllLocations() {
        return locationService.getAllLocations();
    }

    @Operation(summary = "Get location by slug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location found"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @GetMapping("/{slug}")
    public ResponseEntity<Location> getLocationBySlug(@PathVariable String slug) {
        Optional<Location> location = locationService.getLocationBySlug(slug);
        return location.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create location")
    @PostMapping
    public Location createLocation(@RequestBody Location location) {
        return locationService.createLocation(location);
    }

    @Operation(summary = "Update location by slug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location updated"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @PutMapping("/{slug}")
    public ResponseEntity<Location> updateLocation(@PathVariable String slug, @RequestBody Location location) {
        return ResponseEntity.ok(locationService.updateLocation(slug, location));
    }

    @Operation(summary = "Delete location by slug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Location deleted"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> deleteLocation(@PathVariable String slug) {
        locationService.deleteLocation(slug);
        return ResponseEntity.noContent().build();
    }
}
