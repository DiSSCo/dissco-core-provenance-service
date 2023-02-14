package eu.dissco.core.provenanceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.annotation.EnableKafkaRetryTopic;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableKafkaRetryTopic
@ConfigurationPropertiesScan
@SpringBootApplication
@EnableMongoRepositories
public class EventProvenanceConsumerApplication {

  public static void main(String[] args) {
    SpringApplication.run(EventProvenanceConsumerApplication.class, args);
  }

}
