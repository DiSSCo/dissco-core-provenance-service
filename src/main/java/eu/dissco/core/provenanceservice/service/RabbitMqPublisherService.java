package eu.dissco.core.provenanceservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.provenanceservice.properties.RabbitMqProperties;
import eu.dissco.core.provenanceservice.schema.CreateUpdateTombstoneEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMqPublisherService {

  private final RabbitTemplate rabbitTemplate;
  private final RabbitMqProperties rabbitMqProperties;
  private final ObjectMapper mapper;

  public void dlqMessageRaw(String message) {
    rabbitTemplate.convertAndSend(
        rabbitMqProperties.getDlqExchangeName(), rabbitMqProperties.getDlqRoutingKeyName(), message
    );
  }

  public void dlqMessage(CreateUpdateTombstoneEvent event) throws JsonProcessingException {
    var message = mapper.writeValueAsString(event);
    rabbitTemplate.convertAndSend(rabbitMqProperties.getDlqExchangeName(),
        rabbitMqProperties.getDlqRoutingKeyName(), message);

  }

}
