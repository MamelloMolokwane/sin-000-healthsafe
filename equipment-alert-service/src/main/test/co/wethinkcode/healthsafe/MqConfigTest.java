package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.mq.MqConfig;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;

import javax.jms.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MqConfigTest {

    @Test
    void equipmentFailureMessageCanBeSentToQueue() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);
        try (Connection connection = factory.createConnection()) {
            connection.start();
            Session session = connection.createSession(Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(MqConfig.QUEUE);
            MessageProducer producer = session.createProducer(queue);
            String expectedMessage = "W-01: Ventilator is faulty";
            TextMessage message = session.createTextMessage(expectedMessage);
            producer.send(message);
            MessageConsumer consumer = session.createConsumer(queue);
            Message received = consumer.receive(5000);

            assertNotNull(received);
            assertEquals(expectedMessage, ((TextMessage) received).getText());
        }
    }
}