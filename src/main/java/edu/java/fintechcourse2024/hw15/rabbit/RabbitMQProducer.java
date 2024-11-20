package edu.java.fintechcourse2024.hw15.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQProducer {
    private static final String QUEUE_NAME = "test_queue";
    private final Connection connection;
    private final Channel channel;
    private final int index;

    public int getIndex() {
        return index;
    }

    public RabbitMQProducer(Integer index) throws IOException, TimeoutException {
        this.index = index;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("user");
        factory.setPassword("password");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
    }


    public void sendMessage(String message) throws IOException {
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
    }

    public void close() {
        try {
            channel.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
