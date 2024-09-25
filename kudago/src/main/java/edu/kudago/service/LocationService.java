package edu.kudago.service;

import edu.kudago.dto.Location;
import edu.kudago.storage.InMemoryStorage;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class LocationService {
    private final InMemoryStorage<Location, String> storage = new InMemoryStorage<>();

    public Iterable<Location> getAllLocations() {
        return storage.findAll();
    }

    public Optional<Location> getLocationBySlug(String slug) {
        return storage.findById(slug);
    }

    public Location createLocation(Location location) {
        return storage.save(location.slug(), location);
    }

    public Location updateLocation(String slug, Location location) {
        return storage.save(slug, location);
    }

    public void deleteLocation(String slug) {
        storage.deleteById(slug);
    }
}
