package edu.kudago.service;

import edu.kudago.dto.Location;
import edu.kudago.exceptions.ResourceNotFoundException;
import edu.kudago.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class LocationServiceTest {

    private LocationService locationService;
    private InMemoryStorage<Location, String> storage;

    @BeforeEach
    public void setUp() {
        storage = Mockito.mock(InMemoryStorage.class);
        locationService = new LocationService(storage);
    }

    @Test
    public void testGetAllLocations() {
        // Arrange
        Location location1 = new Location("location-1", "Location 1");
        Location location2 = new Location("location-2", "Location 2");
        when(storage.findAll()).thenReturn(List.of(location1, location2));

        // Act
        Iterable<Location> locations = locationService.getAllLocations();

        // Assert
        assertEquals(2, ((Collection<?>) locations).size());
    }

    @Test
    public void testGetLocationBySlug() {
        // Arrange
        Location location = new Location("location-1", "Location 1");
        when(storage.findById("location-1")).thenReturn(Optional.of(location));

        // Act
        Location result = locationService.getLocationBySlug("location-1");

        // Assert
        assertEquals(location, result);
    }

    @Test
    public void testGetLocationBySlug_NotFound() {
        // Arrange
        when(storage.findById("location-1")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> locationService.getLocationBySlug("location-1"));
        assertEquals("Location not found with slug: location-1", exception.getMessage());
    }

    @Test
    public void testCreateLocation() {
        // Arrange
        Location location = new Location("location-1", "Location 1");
        when(storage.save(eq("location-1"), any())).thenReturn(location);

        // Act
        Location result = locationService.createLocation(location);

        // Assert
        assertEquals(location, result);
        verify(storage, times(1)).save(eq("location-1"), any());
    }

    @Test
    public void testUpdateLocation() {
        // Arrange
        Location location = new Location("location-1", "Location 1");
        when(storage.existsById("location-1")).thenReturn(true);
        when(storage.save(eq("location-1"), any())).thenReturn(location);

        // Act
        Location result = locationService.updateLocation("location-1", location);

        // Assert
        assertEquals(location, result);
        verify(storage, times(1)).save(eq("location-1"), any());
    }

    @Test
    public void testUpdateLocation_NotFound() {
        // Arrange
        Location location = new Location("location-1", "Location 1");
        when(storage.existsById("location-1")).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> locationService.updateLocation("location-1", location));
        assertEquals("Location not found with slug: location-1", exception.getMessage());
    }

    @Test
    public void testDeleteLocation() {
        // Arrange
        when(storage.existsById("location-1")).thenReturn(true);

        // Act
        locationService.deleteLocation("location-1");

        // Assert
        verify(storage, times(1)).deleteById("location-1");
    }

    @Test
    public void testDeleteLocation_NotFound() {
        // Arrange
        when(storage.existsById("location-1")).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> locationService.deleteLocation("location-1"));
        assertEquals("Location not found with slug: location-1", exception.getMessage());
    }
}
