package edu.kudago.command;

import edu.kudago.client.ApiClient;
import edu.kudago.dto.Location;
import edu.kudago.îbserver.Observer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitializeLocationsCommand implements Command {
    private final ApiClient apiClient;
    private final List<Observer<Location>> observers;

    @Override
    public void execute() {
        Location[] locations = apiClient.fetchLocations();
        if (locations != null) {
            log.info("Fetched {} locations from API", locations.length);
            observers.forEach(observer -> observer.update(Arrays.asList(locations)));
        } else {
            log.warn("No locations fetched from API");
        }
    }
}
