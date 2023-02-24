package eu.dissco.core.provenanceservice.configuration;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import eu.dissco.core.provenanceservice.properties.MongoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MongoConfiguration {

  private final MongoProperties properties;

  @Bean
  public MongoDatabase configureVersionDb() {
    var client = MongoClients.create(properties.getConnectionString());
    return client.getDatabase(properties.getDatabase());
  }

}
