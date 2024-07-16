package eu.dissco.core.provenanceservice.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import eu.dissco.core.provenanceservice.schema.CreateUpdateTombstoneEvent;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventRepository {

  private final MongoDatabase database;
  private final ObjectMapper mapper;

  public boolean insertNewVersion(String versionId, CreateUpdateTombstoneEvent event,
      String collectionName)
      throws JsonProcessingException {
    var collection = database.getCollection(collectionName);
    var document = Document.parse(mapper.writeValueAsString(event));
    document.append("_id", versionId);
    var filter = new Document("_id", versionId);
    var replaceOptions = new ReplaceOptions();
    replaceOptions.upsert(true);
    var result = collection.replaceOne(filter, document, replaceOptions);
    return result.wasAcknowledged();
  }
}
