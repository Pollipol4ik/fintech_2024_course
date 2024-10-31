package edu.kudago.service;

import edu.kudago.dto.Location;
import edu.kudago.exceptions.ResourceNotFoundException;
import edu.kudago.memento.LocationHistoryService;
import edu.kudago.storage.InMemoryStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final InMemoryStorage<Location, String> storage = new InMemoryStorage<>();
    private final LocationHistoryService historyService;

    public Iterable<Location> getAllLocations() {
        return storage.findAll();
    }

    public Location getLocationBySlug(String slug) {
        return storage.findById(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with slug: " + slug));
    }

    public Location createLocation(Location location) {
        Location createdLocation = storage.save(location.slug(), location);
        historyService.saveMemento(createdLocation);
        return createdLocation;
    }

    public Location updateLocation(String slug, Location location) {
        if (!storage.existsById(slug)) {
            throw new ResourceNotFoundException("Location not found with slug: " + slug);
        }
        Location updatedLocation = storage.save(slug, location);
        historyService.saveMemento(updatedLocation);
        return storage.save(slug, location);
    }

    public void deleteLocation(String slug) {
        Location location = storage.findById(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with slug: " + slug));
        historyService.saveMemento(location);
        storage.deleteById(slug);

    }
}
