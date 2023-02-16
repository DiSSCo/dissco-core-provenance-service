package eu.dissco.core.provenanceservice.configuration;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import eu.dissco.core.provenanceservice.properties.MongoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MongoConfiguration {

  private final MongoProperties properties;

  @Bean
  public MongoCollection<Document> configureVersionDb() {
    var client = MongoClients.create(properties.getConnectionString());
    var database = client.getDatabase(properties.getDatabase());
    return database.getCollection(properties.getCollection());
  }

}
