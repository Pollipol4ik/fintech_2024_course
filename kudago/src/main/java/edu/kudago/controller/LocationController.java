package edu.kudago.controller;

import edu.kudago.dto.Location;
import edu.kudago.service.LocationService;
import edu.simplestarter.aspect.LogExecutionTime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
            @ApiResponse(responseCode = "200", description = "Location found")
    })
    @GetMapping("/{slug}")
    public ResponseEntity<Location> getLocationBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(locationService.getLocationBySlug(slug));
    }

    @Operation(summary = "Create location")
    @PostMapping
    public Location createLocation(@RequestBody Location location) {
        return locationService.createLocation(location);
    }

    @Operation(summary = "Update location by slug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location updated")
    })
    @PutMapping("/{slug}")
    public ResponseEntity<Location> updateLocation(@PathVariable String slug, @RequestBody Location location) {
        return ResponseEntity.ok(locationService.updateLocation(slug, location));
    }

    @Operation(summary = "Delete location by slug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Location deleted")
    })
    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> deleteLocation(@PathVariable String slug) {
        locationService.deleteLocation(slug);
        return ResponseEntity.noContent().build();
    }
}
