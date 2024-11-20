package edu.java.fintechcourse2024.hw15.kafka;

import edu.java.fintechcourse2024.hw15.BaseBenchmark;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class KafkaProducerConsumerBenchmarkBase extends BaseBenchmark {
    private List<KafkaProducer<String, String>> producers;
    private List<KafkaConsumer<String, String>> consumers;

    protected final int numberOfProducers;
    protected final int numberOfConsumers;

    public KafkaProducerConsumerBenchmarkBase(int numberOfProducers, int numberOfConsumers) {
        this.numberOfProducers = numberOfProducers;
        this.numberOfConsumers = numberOfConsumers;
    }

    @Setup(Level.Trial)
    public void setup() {
        producers = new ArrayList<>();
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", "localhost:29092");
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        for (int i = 0; i < numberOfProducers; i++) {
            producers.add(new KafkaProducer<>(producerProps));
        }

        consumers = new ArrayList<>();
        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", "localhost:29092");
        consumerProps.put("group.id", "test-group");
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        for (int i = 0; i < numberOfConsumers; i++) {
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
            consumer.subscribe(List.of("test-topic"));
            consumers.add(consumer);
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        producers.forEach(KafkaProducer::close);
        consumers.forEach(KafkaConsumer::close);
    }

    @Benchmark
    public void kafkaProducersConsumers(Blackhole blackhole) {
        producers.forEach(producer -> {
            var sendFuture = producer.send(new ProducerRecord<>("test-topic", "key", "value"));
            blackhole.consume(sendFuture);
            blackhole.consume(producer);
        });
        consumers.forEach(consumer -> {
            var message = consumer.poll(Duration.ofMillis(100));
            blackhole.consume(message);
            blackhole.consume(consumer);
        });
        blackhole.consume(producers);
        blackhole.consume(consumers);
    }
}

