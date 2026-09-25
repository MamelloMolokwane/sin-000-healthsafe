package co.wethinkcode.healthsafe.mq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Shared by every producer/consumer service that talks to the "equipment-failure-queue"
 * ActiveMQ queue. Duplicated into each participating service's own source tree,
 * since these are independent Maven projects with no shared parent pom.
 */
public final class MqConfig {

    public static final String BROKER_URL = "tcp://localhost:61616";
    public static final String QUEUE = "equipment-failure-queue";
    private ConnectionFactory connectionFactory;

    public MqConfig() {
        connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
    }

    public void subscribe() {
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(Session.CLIENT_ACKNOWLEDGE);
            Destination destination = session.createQueue(QUEUE);
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage textMessage) {
                        System.out.println("Processed Message: " + textMessage.getText());
                        message.acknowledge();
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
