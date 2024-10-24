package edu.kudago.service;

import edu.kudago.IntegrationEnvironment;
import edu.kudago.dto.EventDto;
import edu.kudago.exceptions.ResourceNotFoundException;
import edu.kudago.repository.EventRepository;
import edu.kudago.repository.LocationRepository;
import edu.kudago.repository.entity.EventEntity;
import edu.kudago.repository.entity.LocationEntity;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springdoc.core.converters.models.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DirtiesContext
public class EventServiceTest extends IntegrationEnvironment {

    @Autowired
    private EventService eventService;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private LocationRepository locationRepository;

    @Test
    @Transactional
    @Rollback
    public void createEvent_shouldSaveEvent_whenValidDtoProvided() {
        // Arrange
        Long locationId = 1L;
        LocationEntity location = new LocationEntity();
        location.setId(locationId);
        location.setName("Test Location");

        EventDto eventDto = new EventDto("Test Event", LocalDateTime.now(), locationId);

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        Mockito.when(eventRepository.save(Mockito.any(EventEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EventEntity createdEvent = eventService.createEvent(eventDto);

        // Assert
        assertThat(createdEvent).isNotNull();
        assertThat(createdEvent.getName()).isEqualTo(eventDto.name());
        assertThat(createdEvent.getPlace()).isEqualTo(location);
    }

    @Test
    @Transactional
    @Rollback
    public void createEvent_shouldThrowResourceNotFoundException_whenLocationDoesNotExist() {
        // Arrange
        EventDto eventDto = new EventDto("Test Event", LocalDateTime.now(), 1L);
        Mockito.when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        // Expected
        assertThatThrownBy(() -> eventService.createEvent(eventDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Place not found with id: 1");
    }

    @Test
    @Transactional
    @Rollback
    public void getAllEvents_shouldReturnAllEvents() {
        // Arrange
        EventEntity event1 = new EventEntity();
        event1.setId(1L);
        event1.setName("Event 1");

        EventEntity event2 = new EventEntity();
        event2.setId(2L);
        event2.setName("Event 2");

        Mockito.when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        // Act
        List<EventEntity> events = eventService.getAllEvents();

        // Assert
        assertThat(events).hasSize(2);
        assertThat(events).containsExactlyInAnyOrder(event1, event2);
    }

    @Test
    @Transactional
    @Rollback
    public void getEventById_shouldReturnEvent_whenEventExists() {
        // Arrange
        EventEntity event = new EventEntity();
        event.setId(1L);
        event.setName("Test Event");

        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        // Act
        EventEntity foundEvent = eventService.getEventById(1L);

        // Assert
        assertThat(foundEvent).isEqualTo(event);
    }

    @Test
    @Transactional
    @Rollback
    public void getEventById_shouldThrowResourceNotFoundException_whenEventDoesNotExist() {
        // Arrange
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        // Expected
        assertThatThrownBy(() -> eventService.getEventById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Event not found with id: 1");
    }

    @Test
    @Transactional
    @Rollback
    public void updateEvent_shouldUpdateEvent_whenValidDtoProvided() {
        // Arrange
        Long locationId = 1L;
        LocationEntity location = new LocationEntity();
        location.setId(locationId);

        EventEntity existingEvent = new EventEntity();
        existingEvent.setId(1L);
        existingEvent.setName("Old Event");

        EventDto eventDto = new EventDto("Updated Event", LocalDateTime.now(), locationId);

        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        Mockito.when(eventRepository.save(existingEvent)).thenReturn(existingEvent);

        // Act
        EventEntity updatedEvent = eventService.updateEvent(1L, eventDto);

        // Assert
        assertThat(updatedEvent.getName()).isEqualTo("Updated Event");
        assertThat(updatedEvent.getPlace()).isEqualTo(location);
    }

    @Test
    @Transactional
    @Rollback
    public void updateEvent_shouldThrowResourceNotFoundException_whenEventDoesNotExist() {
        // Arrange
        EventDto eventDto = new EventDto("Updated Event", LocalDateTime.now(), 1L);
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        // Expected
        assertThatThrownBy(() -> eventService.updateEvent(1L, eventDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Event not found with id: 1");
    }

    @Test
    @Transactional
    @Rollback
    public void deleteEvent_shouldRemoveEvent_whenEventExists() {
        // Arrange
        EventEntity event = new EventEntity();
        event.setId(1L);

        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        // Act
        eventService.deleteEvent(1L);

        // Assert
        Mockito.verify(eventRepository).delete(event);
    }

    @Test
    @Transactional
    @Rollback
    public void deleteEvent_shouldThrowResourceNotFoundException_whenEventDoesNotExist() {
        // Arrange
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        // Expected
        assertThatThrownBy(() -> eventService.deleteEvent(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Event not found with id: 1");
    }


}
