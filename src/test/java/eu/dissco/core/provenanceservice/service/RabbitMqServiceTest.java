package eu.dissco.core.provenanceservice.service;

import static eu.dissco.core.provenanceservice.TestUtils.givenMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    rabbitMqService = new RabbitMqService(processingService);
  }

  @Test
  void testGetMessages() throws Exception {
    // Given
    var message = givenMessage();

    // When
    rabbitMqService.getMessages(message);

    // Then
    then(processingService).should().handleMessage(message);
  }

  @Test
  void testGetMessagesWithException() throws Exception {
    // Given
    var message = givenMessage();
    doThrow(JsonProcessingException.class).when(processingService).handleMessage(message);

    // When / Then
    assertThrows(JsonProcessingException.class, () -> rabbitMqService.getMessages(message));
  }
}
