package edu.java.fintechcourse2024.hw15.kafka.test;

import edu.java.fintechcourse2024.hw15.kafka.KafkaProducerConsumerBenchmarkBase;


public class LoadBalancingMultipleConsumersBenchmark extends KafkaProducerConsumerBenchmarkBase {
    public LoadBalancingMultipleConsumersBenchmark() {
        super(3, 3);
    }
}