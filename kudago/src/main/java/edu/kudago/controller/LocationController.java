package edu.kudago.controller;

import edu.kudago.dto.Location;
import edu.kudago.repository.entity.LocationEntity;
import edu.kudago.security.annotations.IsAdmin;
import edu.kudago.security.annotations.IsUser;
import edu.kudago.service.LocationService;
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

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    @IsAdmin
    public ResponseEntity<LocationEntity> createLocation(@RequestBody Location locationDto) {
        return ResponseEntity.ok(locationService.createLocation(locationDto));
    }

    @GetMapping
    @IsUser
    public ResponseEntity<List<LocationEntity>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/{id}")
    @IsUser
    public ResponseEntity<LocationEntity> getLocationById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @GetMapping("/{id}/with-events")
    @IsUser
    public ResponseEntity<LocationEntity> getLocationByIdWithEvents(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationByIdWithEvents(id));
    }

    @PutMapping("/{id}")
    @IsAdmin
    public ResponseEntity<LocationEntity> updateLocation(@PathVariable Long id, @RequestBody Location locationDto) {
        return ResponseEntity.ok(locationService.updateLocation(id, locationDto));
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
