package edu.java.fintechcourse2024.hw15.rabbit.test;

import edu.java.fintechcourse2024.hw15.rabbit.RabbitMQBenchmarkBase;

public class StressTestBenchmark extends RabbitMQBenchmarkBase {
    public StressTestBenchmark() {
        super(10, 10);
    }
}