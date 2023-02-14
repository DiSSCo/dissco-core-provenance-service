package eu.dissco.core.provenanceservice.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class BaseMongoRepositoryIT {

  private static final DockerImageName MONGODB =
      DockerImageName.parse("mongo:6.0.4");

  @Container
  private static final MongoDBContainer CONTAINER = new MongoDBContainer(MONGODB);
  protected MongoCollection<Document> collection;
  private MongoClient client;

  @BeforeEach
  void prepareDocumentStore(){
    client = MongoClients.create(CONTAINER.getConnectionString());
    var database = client.getDatabase("dissco");
    collection = database.getCollection("versions");
  }

  @AfterEach
  void disposeDocumentStore(){
    client.close();
  }

}
