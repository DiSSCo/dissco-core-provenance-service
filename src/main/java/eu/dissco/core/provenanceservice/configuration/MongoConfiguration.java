package eu.dissco.core.provenanceservice.configuration;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MongoConfiguration {

  @Bean
  public MongoDatabase configureVersionDb() {
    var client = MongoClients.create("mongodb://root:7VG5vabHP2@localhost:27017/?authSource=admin");
    return client.getDatabase("dissco");
  }

}
