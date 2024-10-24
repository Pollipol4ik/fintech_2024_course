package edu.kudago.service;

import edu.kudago.IntegrationEnvironment;
import edu.kudago.dto.Location;
import edu.kudago.exceptions.ResourceNotFoundException;
import edu.kudago.repository.LocationRepository;
import edu.kudago.repository.entity.LocationEntity;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DirtiesContext
public class LocationServiceTest extends IntegrationEnvironment {

    @Autowired
    private LocationService locationService;

    @MockBean
    private LocationRepository locationRepository;

    @Test
    @Transactional
    @Rollback
    public void createLocation_shouldSaveLocation_whenValidDtoProvided() {
        // Arrange
        Location locationDto = new Location("Test Location", "test-location");
        LocationEntity location = new LocationEntity();
        location.setName(locationDto.name());
        location.setSlug(locationDto.slug());

        Mockito.when(locationRepository.save(Mockito.any(LocationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        LocationEntity createdLocation = locationService.createLocation(locationDto);

        // Assert
        assertThat(createdLocation).isNotNull();
        assertThat(createdLocation.getName()).isEqualTo(locationDto.name());
        assertThat(createdLocation.getSlug()).isEqualTo(locationDto.slug());
    }

    @Test
    @Transactional
    @Rollback
    public void getAllLocations_shouldReturnAllLocations() {
        // Arrange
        LocationEntity loc1 = new LocationEntity();
        loc1.setSlug("location-1");
        loc1.setName("Location 1");

        LocationEntity loc2 = new LocationEntity();
        loc2.setSlug("location-2");
        loc2.setName("Location 2");

        Mockito.when(locationRepository.findAll()).thenReturn(List.of(loc1, loc2));

        // Act
        List<LocationEntity> locations = locationService.getAllLocations();

        // Assert
        assertThat(locations).hasSize(2);
        assertThat(locations).containsExactlyInAnyOrder(loc1, loc2);
    }

    @Test
    @Transactional
    @Rollback
    public void getLocationBySlug_shouldReturnLocation_whenLocationExists() {
        // Arrange
        LocationEntity location = new LocationEntity();
        location.setSlug("test-location");
        location.setName("Test Location");

        Mockito.when(locationRepository.findBySlug("test-location")).thenReturn(Optional.of(location));

        // Act
        LocationEntity foundLocation = locationService.getLocationBySlug("test-location");

        // Assert
        assertThat(foundLocation).isEqualTo(location);
    }

    @Test
    @Transactional
    @Rollback
    public void getLocationBySlug_shouldThrowResourceNotFoundException_whenLocationDoesNotExist() {
        // Arrange
        Mockito.when(locationRepository.findBySlug("non-existent-slug")).thenReturn(Optional.empty());

        // Expected
        assertThatThrownBy(() -> locationService.getLocationBySlug("non-existent-slug"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Location not found with slug: non-existent-slug");
    }

    @Test
    @Transactional
    @Rollback
    public void updateLocation_shouldUpdateLocation_whenValidDtoProvided() {
        // Arrange
        LocationEntity existingLocation = new LocationEntity();
        existingLocation.setSlug("test-location");
        existingLocation.setName("Old Location");

        Location locationDto = new Location("Updated Location", "updated-location");

        Mockito.when(locationRepository.findBySlug("test-location")).thenReturn(Optional.of(existingLocation));
        Mockito.when(locationRepository.save(existingLocation)).thenReturn(existingLocation);

        // Act
        LocationEntity updatedLocation = locationService.updateLocation("test-location", locationDto);

        // Assert
        assertThat(updatedLocation.getName()).isEqualTo(locationDto.name());
        assertThat(updatedLocation.getSlug()).isEqualTo(locationDto.slug());
    }

    @Test
    @Transactional
    @Rollback
    public void updateLocation_shouldThrowResourceNotFoundException_whenLocationDoesNotExist() {
        // Arrange
        Location locationDto = new Location("Updated Location", "updated-location");
        Mockito.when(locationRepository.findBySlug("non-existent-slug")).thenReturn(Optional.empty());

        // Expected
        assertThatThrownBy(() -> locationService.updateLocation("non-existent-slug", locationDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Location not found with slug: non-existent-slug");
    }

    @Test
    @Transactional
    @Rollback
    public void deleteLocation_shouldRemoveLocation_whenLocationExists() {
        // Arrange
        LocationEntity location = new LocationEntity();
        location.setSlug("test-location");

        Mockito.when(locationRepository.findBySlug("test-location")).thenReturn(Optional.of(location));

        // Act
        locationService.deleteLocation("test-location");

        // Assert
        Mockito.verify(locationRepository).delete(location);
    }

    @Test
    @Transactional
    @Rollback
    public void deleteLocation_shouldThrowResourceNotFoundException_whenLocationDoesNotExist() {
        // Arrange
        Mockito.when(locationRepository.findBySlug("non-existent-slug")).thenReturn(Optional.empty());

        // Expected
        assertThatThrownBy(() -> locationService.deleteLocation("non-existent-slug"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Location not found with slug: non-existent-slug");
    }
}
