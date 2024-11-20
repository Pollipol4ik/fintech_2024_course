package edu.java.fintechcourse2024.hw15.rabbit;

import edu.java.fintechcourse2024.hw15.BaseBenchmark;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public abstract class RabbitMQBenchmarkBase extends BaseBenchmark {
    private List<RabbitMQProducer> producers;
    private List<RabbitMQConsumer> consumers;

    protected final int producerCount;
    protected final int consumerCount;

    public RabbitMQBenchmarkBase(int producerCount, int consumerCount) {
        this.producerCount = producerCount;
        this.consumerCount = consumerCount;
    }

    @Setup(Level.Trial)
    public void setup() throws IOException, TimeoutException {
        producers = new ArrayList<>();
        consumers = new ArrayList<>();

        for (int i = 0; i < producerCount; i++) {
            producers.add(new RabbitMQProducer(i));
        }

        for (int i = 0; i < consumerCount; i++) {
            consumers.add(new RabbitMQConsumer());
        }
    }

    @TearDown(Level.Trial)
    public void teardown() {
        producers.forEach(RabbitMQProducer::close);
        consumers.forEach(RabbitMQConsumer::close);
    }

    @Benchmark
    public void rabbitProducerConsumer(Blackhole blackhole) {
        producers.forEach(producer -> {
            try {
                var message = "Тестовое сообщение " + producer.getIndex();
                producer.sendMessage(message);
                blackhole.consume(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            blackhole.consume(producer);
        });
        consumers.forEach(consumer -> {
            try {
                var message = consumer.consumeMessage();
                blackhole.consume(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            blackhole.consume(consumer);
        });
        blackhole.consume(producers);
        blackhole.consume(consumers);
    }
}