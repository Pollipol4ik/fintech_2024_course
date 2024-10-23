package edu.kudago.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class ExecutorServiceConfig {

    @Value("${app.fixedThreadPoolSize}")
    private int fixedThreadPoolSize;

    @Value("${app.scheduledThreadPoolSize}")
    private int scheduledThreadPoolSize;

    @Bean(name = "fixedThreadPool")
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(fixedThreadPoolSize, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("FixedPoolThread-" + thread.getId());
            return thread;
        });
    }

    @Bean(name = "scheduledThreadPool")
    public ScheduledExecutorService scheduledThreadPool() {
        return Executors.newScheduledThreadPool(scheduledThreadPoolSize, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("ScheduledPoolThread-" + thread.getId());
            return thread;
        });
    }
}
