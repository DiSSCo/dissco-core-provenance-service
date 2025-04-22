package eu.dissco.core.provenanceservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.dissco.core.provenanceservice.exception.MongodbException;
import eu.dissco.core.provenanceservice.exception.UnknownSubjectException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class RabbitMqService {

  private final ProcessingService processingService;

  @RabbitListener(queues = "${rabbitmq.queue.name}")
  public void getMessages(@Payload String message)
      throws JsonProcessingException, MongodbException, UnknownSubjectException {
    processingService.handleMessage(message);
  }

}
