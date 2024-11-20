package edu.java.fintechcourse2024.hw15.rabbit.test;

import edu.java.fintechcourse2024.hw15.rabbit.RabbitMQBenchmarkBase;

public class MultipleConsumersBenchmark extends RabbitMQBenchmarkBase {
    public MultipleConsumersBenchmark() {
        super(1, 3);
    }
}