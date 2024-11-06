package edu.kudago.observer;

import edu.kudago.dto.Location;
import edu.kudago.memento.HistoryService;
import edu.kudago.memento.LocationMemento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LocationObserver implements Observer<Location> {
    private final HistoryService<Location, LocationMemento> historyService;

    @Override
    public void update(List<Location> locations) {
        locations.forEach(historyService::saveMemento);
    }
}
