package com.example.eventshub.messaging;

import com.example.eventshub.EventshubApplication;
import com.example.eventshub.model.Notification;
import com.example.eventshub.repository.NotificationRepository;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * End-to-end test: produce EventCreatedMessage -> consume -> Notification saved.
 */
@SpringBootTest(classes = EventshubApplication.class)
@ActiveProfiles("test")
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:0,CONTROLLER://localhost:0,EXTERNAL://localhost:0",
                "advertised.listeners=PLAINTEXT://localhost:0,EXTERNAL://localhost:0",
                "listener.security.protocol.map=PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT,EXTERNAL:PLAINTEXT",
                "inter.broker.listener.name=PLAINTEXT",
                "controller.listener.names=CONTROLLER",
                "log.dir=${java.io.tmpdir}/kafka-logs-${random.uuid}"
        }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EventFlowIntegrationTest {

    private static final String TOPIC = "event-created";

    @Autowired
    private EventProducer producer;

    @Autowired
    private NotificationRepository notificationRepository;

    @Configuration
    static class TopicConfig {
        @Bean
        NewTopic eventCreatedTopic() {
            return new NewTopic(TOPIC, 1, (short) 1);
        }
    }

    @DynamicPropertySource
    static void kafkaProps(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers",
                () -> System.getProperty("spring.embedded.kafka.brokers"));
        registry.add("app.kafka.topics.event-created", () -> TOPIC);
        registry.add("spring.kafka.consumer.group-id", () -> "eventshub-test");
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
    }

    @AfterEach
    void cleanDb() {
        notificationRepository.deleteAll();
    }

    @Test
    void shouldCreateNotificationOnEventCreatedMessage() {
        // Arrange
        EventCreatedMessage msg = new EventCreatedMessage(
                42L,
                "Test event",
                "Description",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                "Online"
        );

        // Act
        producer.sendEventCreated(msg);

        // Assert (attesa consumo e salvataggio)
        await().atMost(10, SECONDS).untilAsserted(() -> {
            List<Notification> all = notificationRepository.findAll();
            assertThat(all).hasSize(1);
            Notification n = all.getFirst();
            assertThat(n.getEventId()).isEqualTo(42L);
            assertThat(n.getTitle()).contains("Test event");
            assertThat(n.getMessage()).contains("Online");
        });
    }
}