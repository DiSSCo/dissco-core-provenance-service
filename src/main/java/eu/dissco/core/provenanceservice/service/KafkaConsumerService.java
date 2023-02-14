package eu.dissco.core.provenanceservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.dissco.core.provenanceservice.exception.MongodbException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class KafkaConsumerService {

  private final ProcessingService processingService;

  @RetryableTopic(
      backoff = @Backoff(value = 3000L),
      attempts = "3",
      autoCreateTopics = "true")
  @KafkaListener(topics = "${spring.kafka.consumer.topic}")
  public void getMessages(@Payload String message)
      throws JsonProcessingException, MongodbException {
    processingService.handleMessage(message);
  }

  @DltHandler
  public void dltHandler(String message,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String receivedTopic) {
    log.info("Message {} received in dlt handler at topic {} ", message, receivedTopic);
  }


}
