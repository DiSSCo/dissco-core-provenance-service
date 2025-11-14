package eu.dissco.core.provenanceservice.service;

import static eu.dissco.core.provenanceservice.TestUtils.MAPPER;
import static eu.dissco.core.provenanceservice.TestUtils.givenEvent;
import static org.assertj.core.api.Assertions.assertThat;

import eu.dissco.core.provenanceservice.properties.RabbitMqProperties;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(MockitoExtension.class)
class RabbitMqPublisherServiceTest {

  private static RabbitMQContainer container;
  private static RabbitTemplate rabbitTemplate;
  private RabbitMqPublisherService rabbitMqPublisherService;

  @BeforeAll
  static void setupContainer() throws IOException, InterruptedException {
    container = new RabbitMQContainer("rabbitmq:4.0.8-management-alpine");
    container.start();
    declareRabbitResources();
    CachingConnectionFactory factory = new CachingConnectionFactory(container.getHost());
    factory.setPort(container.getAmqpPort());
    factory.setUsername(container.getAdminUsername());
    factory.setPassword(container.getAdminPassword());
    rabbitTemplate = new RabbitTemplate(factory);
    rabbitTemplate.setReceiveTimeout(100L);
  }


  private static void declareRabbitResources()
      throws IOException, InterruptedException {
    var exchangeName = "create-update-tombstone-exchange-dlq";
    var queueName = "create-update-tombstone-queue-dlq";
    var routingKey = "create-update-tombstone-dlq";
    container.execInContainer("rabbitmqadmin", "declare", "exchange", "name=" + exchangeName,
        "type=direct", "durable=true");
    container.execInContainer("rabbitmqadmin", "declare", "queue", "name=" + queueName,
        "queue_type=quorum", "durable=true");
    container.execInContainer("rabbitmqadmin", "declare", "binding", "source=" + exchangeName,
        "destination_type=queue", "destination=" + queueName, "routing_key=" + routingKey);
  }

  @AfterAll
  static void shutdownContainer() {
    container.stop();
  }

  @BeforeEach
  void setup() {
    rabbitMqPublisherService = new RabbitMqPublisherService(rabbitTemplate, new RabbitMqProperties(), MAPPER);
  }

  @Test
  void testPublishDlqRaw(){
    // Given

    // When
    rabbitMqPublisherService.dlqMessageRaw("some message");

    // Then
    var dlqMessage = rabbitTemplate.receive("create-update-tombstone-queue-dlq");
    assertThat(new String(dlqMessage.getBody())).isNotNull();
  }

  @Test
  void testPublishDlq() throws Exception {
    // Given

    // When
    rabbitMqPublisherService.dlqMessage(givenEvent());

    // Then
    var dlqMessage = rabbitTemplate.receive("create-update-tombstone-queue-dlq");
    assertThat(new String(dlqMessage.getBody())).isNotNull();
  }


}
