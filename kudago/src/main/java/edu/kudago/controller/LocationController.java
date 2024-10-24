package edu.kudago.controller;

import edu.kudago.dto.Location;
import edu.kudago.repository.entity.LocationEntity;
import edu.kudago.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<LocationEntity> createLocation(@RequestBody Location locationDto) {
        return ResponseEntity.ok(locationService.createLocation(locationDto));
    }

    @GetMapping
    public ResponseEntity<List<LocationEntity>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationEntity> getLocationById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @GetMapping("/{id}/with-events")
    public ResponseEntity<LocationEntity> getLocationByIdWithEvents(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationByIdWithEvents(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationEntity> updateLocation(@PathVariable Long id, @RequestBody Location locationDto) {
        return ResponseEntity.ok(locationService.updateLocation(id, locationDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
