package eu.dissco.core.provenanceservice.component;

import static eu.dissco.core.provenanceservice.TestUtils.givenMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;

@ExtendWith(MockitoExtension.class)
class MessageCompressionComponentTest {

  private MessageCompressionComponent messageCompressionComponent;

  @BeforeEach
  void setUp() {
    messageCompressionComponent = new MessageCompressionComponent();
  }

  @Test
  void testCompressMessage() {
    // Given
    var messageString = givenMessage();

    // When
    var compressedMessage = messageCompressionComponent.toMessage(messageString,
        new MessageProperties());

    // Then
    assertThat(compressedMessage.getMessageProperties().getContentEncoding()).isEqualTo("gzip");
    assertThat(compressedMessage.getMessageProperties().getContentType()).isEqualTo(
        "application/json");
    var decompressedMessage = messageCompressionComponent.fromMessage(compressedMessage);
    assertThat(((String) decompressedMessage).strip()).isEqualTo(messageString.strip());
  }

  @Test
  void testInvalidMessage() {
    // Given
    var messageInteger = 1234;
    var messageProperties = new MessageProperties();

    // When / Then
    assertThrows(MessageConversionException.class,
        () -> messageCompressionComponent.toMessage(messageInteger, messageProperties));
  }

  @Test
  void testPlainMessage() {
    // Given
    var messageProperties = new MessageProperties();
    messageProperties.setContentType("text/plain");
    var message = new Message(givenMessage().getBytes(StandardCharsets.UTF_8), messageProperties);

    // When
    var result = messageCompressionComponent.fromMessage(message);

    // Then
    assertThat(result).isEqualTo(givenMessage());
  }
}
