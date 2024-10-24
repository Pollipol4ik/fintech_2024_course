package edu.kudago.service;

import edu.kudago.dto.EventDto;
import edu.kudago.exceptions.ResourceNotFoundException;
import edu.kudago.repository.EventRepository;
import edu.kudago.repository.LocationRepository;
import edu.kudago.repository.entity.EventEntity;
import edu.kudago.repository.entity.LocationEntity;
import edu.kudago.specification.EventSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;

    @Transactional
    public EventEntity createEvent(EventDto eventDto) {
        LocationEntity location = locationRepository.findById(eventDto.placeId())
                .orElseThrow(() -> new ResourceNotFoundException("Place not found with id: " + eventDto.placeId()));
        EventEntity event = new EventEntity();
        event.setName(eventDto.name());
        event.setDate(eventDto.date());
        event.setPlace(location);
        return eventRepository.save(event);
    }

    public List<EventEntity> getAllEvents() {
        return eventRepository.findAll();
    }

    public EventEntity getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
    }

    @Transactional
    public EventEntity updateEvent(Long id, EventDto eventDto) {
        EventEntity event = getEventById(id);
        LocationEntity location = locationRepository.findById(eventDto.placeId())
                .orElseThrow(() -> new ResourceNotFoundException("Place not found with id: " + eventDto.placeId()));
        event.setName(eventDto.name());
        event.setDate(eventDto.date());
        event.setPlace(location);
        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        EventEntity event = getEventById(id);
        eventRepository.delete(event);
    }

    public List<EventEntity> searchEvents(String name, Long placeId, LocalDateTime fromDate, LocalDateTime toDate) {
        Specification<EventEntity> spec = Specification.where(EventSpecification.hasName(name))
                .and(EventSpecification.hasPlace(placeId))
                .and(EventSpecification.betweenDates(fromDate, toDate));
        return eventRepository.findAll(spec);
    }
}
