package edu.kudago.initializer;

import edu.kudago.command.Command;
import edu.simplestarter.aspect.LogExecutionTime;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@LogExecutionTime
@Log4j2
@RequiredArgsConstructor
public class DataInitializer {

    private final Command initializeCategoriesCommand;
    private final Command initializeLocationsCommand;

    @PostConstruct
    public void initData() {
        log.info("Starting data initialization...");

        initializeCategoriesCommand.execute();
        initializeLocationsCommand.execute();

        log.info("Data initialization completed.");
    }


}
