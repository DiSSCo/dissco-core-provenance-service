package eu.dissco.core.provenanceservice.repository;

import static eu.dissco.core.provenanceservice.TestUtils.MAPPER;
import static eu.dissco.core.provenanceservice.TestUtils.PID;
import static eu.dissco.core.provenanceservice.TestUtils.givenEvent;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventRepositoryIT extends BaseMongoRepositoryIT {

  private EventRepository repository;

  @BeforeEach
  void setup() {
    repository = new EventRepository(database, MAPPER);
  }

  @Test
  void testInsertNew() throws JsonProcessingException {
    // Given
    var event = givenEvent();
    var expected = Document.parse(MAPPER.writeValueAsString(event));
    expected.append("_id", PID);
    var collectionName = "digital_specimen_provenance";
    var collection = database.getCollection(collectionName);

    // When
    repository.insertNewVersion(PID, event, collectionName);

    // Then
    var result = collection.find();
    assertThat(result.first()).isEqualTo(expected);
  }

  @Test
  void testInsertUpdated() throws JsonProcessingException {
    // Given
    var event = givenEvent();
    var expected = Document.parse(MAPPER.writeValueAsString(event));
    expected.append("_id", PID);
    var collectionName = "digital_specimen_provenance";
    var collection = database.getCollection(collectionName);
    repository.insertNewVersion(PID, event, collectionName);

    // When
    repository.insertNewVersion(PID, event, collectionName);

    // Then
    var result = collection.find();
    assertThat(result.first()).isEqualTo(expected);
  }

}
