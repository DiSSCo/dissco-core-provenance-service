package eu.dissco.core.provenanceservice.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMqProperties {

  @Positive
  private int batchSize = 500;

  @NotBlank
  private String dlqExchangeName = "provenance-exchange-dlq";

  @NotBlank
  private String dlqRoutingKeyName = "provenance-dlq";

}
