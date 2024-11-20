package edu.java.fintechcourse2024.hw15.rabbit.test;

import edu.java.fintechcourse2024.hw15.rabbit.RabbitMQBenchmarkBase;

public class LoadBalancingMultipleConsumersBenchmark extends RabbitMQBenchmarkBase {
    public LoadBalancingMultipleConsumersBenchmark() {
        super(3, 3);
    }
}