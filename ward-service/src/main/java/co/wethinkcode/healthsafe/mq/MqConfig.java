package co.wethinkcode.healthsafe.mq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Shared by every producer/consumer service that talks to the ActiveMQ broker.
 * Duplicated into each participating service's own source tree, since these are
 * independent Maven projects with no shared parent pom.
 *
 * ward-service is a consumer of TOPIC (staffing updates) and a producer on QUEUE
 * (equipment failures detected on its wards).
 */
public final class MqConfig {

    public static final String BROKER_URL = "tcp://localhost:61616";
    public static final String TOPIC = "staffing-events-topic";
    public static final String QUEUE = "equipment-failure-queue";
    private ConnectionFactory connectionFactory;

    public MqConfig() {
        connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
    }

    public void publish(String message) {
        try (Connection connection = connectionFactory.createConnection()) {
            connection.start();
            Session session = connection.createSession();
            Destination destination = session.createQueue(QUEUE);
            MessageProducer producer = session.createProducer(destination);
            TextMessage textMessage = session.createTextMessage(message);
            producer.send(textMessage);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public void subscribe() {
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession();
            Destination destination = session.createTopic(TOPIC);
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage textMessage) {
                        System.out.println("Processed Message: " + textMessage.getText());
                    }
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
