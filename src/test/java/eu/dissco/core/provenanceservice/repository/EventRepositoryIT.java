package eu.dissco.core.provenanceservice.repository;

import static eu.dissco.core.provenanceservice.TestUtils.MAPPER;
import static eu.dissco.core.provenanceservice.TestUtils.MEDIA_COL;
import static eu.dissco.core.provenanceservice.TestUtils.PID;
import static eu.dissco.core.provenanceservice.TestUtils.SPECIMEN_COL;
import static eu.dissco.core.provenanceservice.TestUtils.givenEvent;
import static eu.dissco.core.provenanceservice.TestUtils.givenProvRecord;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventRepositoryIT extends BaseMongoRepositoryIT {

  private EventRepository repository;

  @BeforeEach
  void setup() {
    repository = new EventRepository(database);
  }

  @Test
  void testInsertNew() throws Exception {
    // Given
    var specimenProvRecord = givenProvRecord();
    var mediaProvRecord = givenProvRecord(givenEvent("ods:DigitalMedia"), MEDIA_COL);
    var expectedSpecimen = Document.parse(MAPPER.writeValueAsString(givenEvent()));
    expectedSpecimen.append("_id", PID);
    var expectedMedia = Document.parse(MAPPER.writeValueAsString(givenEvent("ods:DigitalMedia")));
    expectedMedia.append("_id", PID);

    // When
    var result = repository.insertNewVersion(List.of(specimenProvRecord, mediaProvRecord));
    var specimenResult = database.getCollection(SPECIMEN_COL).find().first();
    var mediaResult = database.getCollection(MEDIA_COL).find().first();

    // Then
    assertThat(result).isEmpty();
    assertThat(specimenResult).isEqualTo(expectedSpecimen);
    assertThat(mediaResult).isEqualTo(expectedMedia);
    assertThat(((Document) specimenResult.get("prov:Activity"))).containsEntry("prov:endedAtTime",
        "2024-06-11T09:14:00.348Z");
  }

  @Test
  void testInsertUpdated() throws Exception {
    // Given
    var specimenProvRecord = givenProvRecord();
    var expected = Document.parse(MAPPER.writeValueAsString(givenEvent()));
    expected.append("_id", PID);
    repository.insertNewVersion(List.of(specimenProvRecord));

    // When
    var result = repository.insertNewVersion(List.of(specimenProvRecord));
    var specimenResult = database.getCollection(SPECIMEN_COL).find().first();

    // Then
    assertThat(result).isEmpty();
    assertThat(specimenResult).isEqualTo(expected);
  }
}
