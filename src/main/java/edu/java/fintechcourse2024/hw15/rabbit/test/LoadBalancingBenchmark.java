package edu.java.fintechcourse2024.hw15.rabbit.test;

import edu.java.fintechcourse2024.hw15.rabbit.RabbitMQBenchmarkBase;

public class LoadBalancingBenchmark extends RabbitMQBenchmarkBase {
    public LoadBalancingBenchmark() {
        super(3, 1);
    }
}
