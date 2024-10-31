package edu.kudago.memento;

import edu.kudago.dto.Location;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Service
public class LocationHistoryService {
    private final List<LocationMemento> history = new ArrayList<>();

    public void saveMemento(Location location) {
        history.add(new LocationMemento(location.slug(), location.name()));
    }

}