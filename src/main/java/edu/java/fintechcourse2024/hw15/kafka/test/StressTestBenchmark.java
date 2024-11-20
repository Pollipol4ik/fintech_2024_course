package edu.java.fintechcourse2024.hw15.kafka.test;

import edu.java.fintechcourse2024.hw15.kafka.KafkaProducerConsumerBenchmarkBase;

public class StressTestBenchmark extends KafkaProducerConsumerBenchmarkBase {
    public StressTestBenchmark() {
        super(10, 10);
    }
}