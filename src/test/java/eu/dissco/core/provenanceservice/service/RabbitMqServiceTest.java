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

  private RabbitMqService rabbitMqService;
  @Mock
  private ProcessingService processingService;

  @BeforeEach
  void setup() {
    rabbitMqService = new RabbitMqService(processingService, MAPPER);
  }

  @Test
  void testGetMessages() throws Exception {
    // Given
    var message = givenMessage();

    // When
    rabbitMqService.getMessages(List.of(message));

    // Then
    then(processingService).should().handleMessages(List.of(givenEvent()));
  }

  @Test
  void testGetMessagesFailed() throws Exception {
    // Given
    var message = "bad message";

    // When
    rabbitMqService.getMessages(List.of(message));

    // Then
    then(processingService).should().handleMessages(List.of());
  }

}
