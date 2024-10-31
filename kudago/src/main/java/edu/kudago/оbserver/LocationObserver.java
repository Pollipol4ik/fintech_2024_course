package edu.kudago.îbserver;

import edu.kudago.dto.Location;
import edu.kudago.memento.LocationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LocationObserver implements Observer<Location> {
    private final LocationHistoryService historyService;

    @Override
    public void update(List<Location> locations) {
        locations.forEach(historyService::saveMemento);
    }
}
