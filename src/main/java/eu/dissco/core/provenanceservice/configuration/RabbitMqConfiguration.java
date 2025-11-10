package eu.dissco.core.provenanceservice.configuration;

import eu.dissco.core.provenanceservice.component.MessageCompressionComponent;
import eu.dissco.core.provenanceservice.properties.RabbitMqProperties;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class RabbitMqConfiguration {

  private final MessageCompressionComponent compressedMessageConverter;
  private final RabbitMqProperties rabbitMqProperties;

  @Bean
  public SimpleRabbitListenerContainerFactory consumerBatchContainerFactory(
      ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setBatchListener(true);
    factory.setBatchSize(rabbitMqProperties.getBatchSize());
    factory.setConsumerBatchEnabled(true);
    factory.setMessageConverter(compressedMessageConverter);
    return factory;
  }
}
