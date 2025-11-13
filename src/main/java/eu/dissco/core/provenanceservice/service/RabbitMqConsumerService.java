package eu.dissco.core.provenanceservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.provenanceservice.exception.MongodbException;
import eu.dissco.core.provenanceservice.properties.RabbitMqProperties;
import eu.dissco.core.provenanceservice.schema.CreateUpdateTombstoneEvent;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class RabbitMqConsumerService {

  private final ProcessingService processingService;
  private final RabbitMqPublisherService rabbitMqPublisherService;
  private final ObjectMapper mapper;

  @RabbitListener(queues = "${rabbitmq.queue.name:create-update-tombstone-queue}", containerFactory = "consumerBatchContainerFactory")
  public void getMessages(@Payload List<String> messages) {
    var events = messages.stream().map(message -> {
      try {
        return mapper.readValue(message, CreateUpdateTombstoneEvent.class);
      } catch (JsonProcessingException e) {
        log.error("Failed to parse event message", e);
        rabbitMqPublisherService.dlqMessageRaw(message);
        return null;
      }
    }).filter(Objects::nonNull).toList();
    processingService.handleMessages(events);
  }

}
