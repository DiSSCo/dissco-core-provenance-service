package eu.dissco.core.provenanceservice.configuration;

import eu.dissco.core.provenanceservice.component.MessageCompressionComponent;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class RabbitMQConfiguration {

  private final MessageCompressionComponent compressedMessageConverter;

  @Bean
  public SimpleRabbitListenerContainerFactory consumerBatchContainerFactory(
      ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(compressedMessageConverter);
    return factory;
  }
}
