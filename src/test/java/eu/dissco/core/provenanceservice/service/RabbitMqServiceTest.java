package eu.dissco.core.provenanceservice.service;

import static eu.dissco.core.provenanceservice.TestUtils.MAPPER;
import static eu.dissco.core.provenanceservice.TestUtils.givenEvent;
import static eu.dissco.core.provenanceservice.TestUtils.givenMessage;
import static org.mockito.BDDMockito.then;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RabbitMqServiceTest {

  private RabbitMqConsumerService rabbitMqService;
  @Mock
  private ProcessingService processingService;
  @Mock
  private RabbitMqPublisherService rabbitMqPublisherService;

  @BeforeEach
  void setup() {
    rabbitMqService = new RabbitMqConsumerService(processingService, rabbitMqPublisherService, MAPPER);
  }

  @Test
  void testGetMessages(){
    // Given
    var message = givenMessage();

    // When
    rabbitMqService.getMessages(List.of(message));

    // Then
    then(processingService).should().handleMessages(List.of(givenEvent()));
  }

  @Test
  void testGetMessagesFailed() {
    // Given
    var message = "bad message";

    // When
    rabbitMqService.getMessages(List.of(message));

    // Then
    then(processingService).should().handleMessages(List.of());
    then(rabbitMqPublisherService).should().dlqMessageRaw(message);
  }

}
