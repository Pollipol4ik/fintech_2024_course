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
    }

    @Test
    @Transactional
    @Rollback
    public void getAllLocations_shouldReturnAllLocations() {
        // Arrange
        LocationEntity loc1 = new LocationEntity();
        loc1.setId(1L);
        loc1.setName("Location 1");

        LocationEntity loc2 = new LocationEntity();
        loc2.setId(2L);
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
    public void getLocationById_shouldReturnLocation_whenLocationExists() {
        // Arrange
        LocationEntity location = new LocationEntity();
        location.setId(1L);
        location.setName("Test Location");

        Mockito.when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        // Act
        LocationEntity foundLocation = locationService.getLocationById(1L);

        // Assert
        assertThat(foundLocation).isEqualTo(location);
    }

    @Test
    @Transactional
    @Rollback
    public void getLocationById_shouldThrowResourceNotFoundException_whenLocationDoesNotExist() {
        // Arrange
        Mockito.when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        // Expected
        assertThatThrownBy(() -> locationService.getLocationById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Location not found with id: 1");
    }

    @Test
    @Transactional
    @Rollback
    public void updateLocation_shouldUpdateLocation_whenValidDtoProvided() {
        // Arrange
        LocationEntity existingLocation = new LocationEntity();
        existingLocation.setId(1L);
        existingLocation.setName("Old Location");

        Location locationDto = new Location("Updated Location", "updated-location");

        Mockito.when(locationRepository.findById(1L)).thenReturn(Optional.of(existingLocation));
        Mockito.when(locationRepository.save(existingLocation)).thenReturn(existingLocation);

        // Act
        LocationEntity updatedLocation = locationService.updateLocation(1L, locationDto);

        // Assert
        assertThat(updatedLocation.getName()).isEqualTo(locationDto.name());
    }

    @Test
    @Transactional
    @Rollback
    public void updateLocation_shouldThrowResourceNotFoundException_whenLocationDoesNotExist() {
        // Arrange
        Location locationDto = new Location("Updated Location", "updated-location");
        Mockito.when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        // Expected
        assertThatThrownBy(() -> locationService.updateLocation(1L, locationDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Location not found with id: 1");
    }

    @Test
    @Transactional
    @Rollback
    public void deleteLocation_shouldRemoveLocation_whenLocationExists() {
        // Arrange
        LocationEntity location = new LocationEntity();
        location.setId(1L);

        Mockito.when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        // Act
        locationService.deleteLocation(1L);

        // Assert
        Mockito.verify(locationRepository).delete(location);
    }

    @Test
    @Transactional
    @Rollback
    public void deleteLocation_shouldThrowResourceNotFoundException_whenLocationDoesNotExist() {
        // Arrange
        Mockito.when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        // Expected
        assertThatThrownBy(() -> locationService.deleteLocation(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Location not found with id: 1");
    }
}
