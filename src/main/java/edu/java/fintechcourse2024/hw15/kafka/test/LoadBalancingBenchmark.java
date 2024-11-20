package edu.java.fintechcourse2024.hw15.kafka.test;

import edu.java.fintechcourse2024.hw15.kafka.KafkaProducerConsumerBenchmarkBase;


public class LoadBalancingBenchmark extends KafkaProducerConsumerBenchmarkBase {
    public LoadBalancingBenchmark() {
        super(3, 1);
    }
}
