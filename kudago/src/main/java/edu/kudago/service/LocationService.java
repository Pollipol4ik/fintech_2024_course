package edu.kudago.service;

import edu.kudago.dto.Location;
import edu.kudago.exceptions.ResourceNotFoundException;
import edu.kudago.storage.InMemoryStorage;
import org.springframework.stereotype.Service;

@Service
public class LocationService {
    private final InMemoryStorage<Location, String> storage = new InMemoryStorage<>();

    public Iterable<Location> getAllLocations() {
        return storage.findAll();
    }

    public Location getLocationBySlug(String slug) {
        return storage.findById(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with slug: " + slug));
    }

    public Location createLocation(Location location) {
        return storage.save(location.slug(), location);
    }

    public Location updateLocation(String slug, Location location) {
        if (!storage.existsById(slug)) {
            throw new ResourceNotFoundException("Location not found with slug: " + slug);
        }
        return storage.save(slug, location);
    }

    public void deleteLocation(String slug) {
        if (!storage.existsById(slug)) {
            throw new ResourceNotFoundException("Location not found with slug: " + slug);
        }
        storage.deleteById(slug);
    }
}
