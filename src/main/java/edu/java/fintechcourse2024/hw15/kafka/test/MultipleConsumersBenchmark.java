package edu.java.fintechcourse2024.hw15.kafka.test;

import edu.java.fintechcourse2024.hw15.kafka.KafkaProducerConsumerBenchmarkBase;

public class MultipleConsumersBenchmark extends KafkaProducerConsumerBenchmarkBase {
    public MultipleConsumersBenchmark() {
        super(1, 3);
    }
}