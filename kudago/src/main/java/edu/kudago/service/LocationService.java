package edu.kudago.service;

import edu.kudago.dto.Location;
import edu.kudago.exceptions.ResourceNotFoundException;
import edu.kudago.repository.LocationRepository;
import edu.kudago.repository.entity.LocationEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    @Transactional
    public LocationEntity createLocation(Location locationDto) {
        LocationEntity location = new LocationEntity();
        location.setName(locationDto.name());
        location.setSlug(locationDto.slug());
        return locationRepository.save(location);
    }

    public List<LocationEntity> getAllLocations() {
        return locationRepository.findAll();
    }

    public LocationEntity getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }

    public LocationEntity getLocationByIdWithEvents(Long id) {
        return locationRepository.findByIdWithEvents(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }

    @Transactional
    public LocationEntity updateLocation(Long id, Location locationDto) {
        LocationEntity location = getLocationById(id);
        location.setName(locationDto.name());
        location.setSlug(locationDto.slug());
        return locationRepository.save(location);
    }

    @Transactional
    public void deleteLocation(Long id) {
        LocationEntity location = getLocationById(id);
        locationRepository.delete(location);
    }
}
