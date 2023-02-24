package eu.dissco.core.provenanceservice.service;

import static eu.dissco.core.provenanceservice.TestUtils.MAPPER;
import static eu.dissco.core.provenanceservice.TestUtils.PID;
import static eu.dissco.core.provenanceservice.TestUtils.givenEvent;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.dissco.core.provenanceservice.exception.MongodbException;
import eu.dissco.core.provenanceservice.exception.UnknownSubjectException;
import eu.dissco.core.provenanceservice.repository.EventRepository;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessingServiceTest {

  @Mock
  private EventRepository repository;

  private ProcessingService service;

  private static Stream<Arguments> provideTestHandleMessage() {
    return Stream.of(
        Arguments.of("DigitalSpecimen", "digital_specimen_provenance"),
        Arguments.of("DigitalMediaObject", "digital_media_provenance"),
        Arguments.of("Annotation", "annotation_provenance")
    );
  }

  @BeforeEach
  void setup() {
    service = new ProcessingService(MAPPER, repository);
  }

  @ParameterizedTest
  @MethodSource("provideTestHandleMessage")
  void testHandleMessage(String subjectType, String collectionName)
      throws JsonProcessingException, MongodbException, UnknownSubjectException {
    // Given
    var message = givenMessage(subjectType);
    var event = givenEvent(subjectType);
    given(repository.insertNewVersion(PID, event, collectionName)).willReturn(true);

    // When
    service.handleMessage(message);

    // Then
    then(repository).should().insertNewVersion(PID, event, collectionName);

  }

  @Test
  void testFailedHandleMessage() throws JsonProcessingException {
    // Given
    var message = givenMessage();
    var event = givenEvent();
    given(repository.insertNewVersion(PID, event, "digital_specimen_provenance")).willReturn(false);

    // When
    assertThatThrownBy(() -> service.handleMessage(message)).isInstanceOf(MongodbException.class);

    // Then
  }

  @Test
  void testHandleMessageUnknownSubjectType() {
    // Given
    var message = givenMessage("Unknown");

    // When
    assertThatThrownBy(() -> service.handleMessage(message)).isInstanceOf(
        UnknownSubjectException.class);

    // Then
  }

  private String givenMessage() {
    return givenMessage("DigitalSpecimen");
  }

  private String givenMessage(String subjectType) {
    return """
        {
          "id": "82adfa56-b1ad-46ee-b05c-e7bbdaa52fb1",
          "eventType": "update",
          "agent": "processing-service",
          "subject": "20.5000.1025/MKA-93P-4MS",
          """ +
        "\"subjectType\":\"" + subjectType + "\"," +
        """
              
              "timestamp": "2023-02-10T08:23:51.817Z",
              "eventRecord": {
                "id": "20.5000.1025/MKA-93P-4MS",
                "midsLevel": 0,
                "version": 7,
                "created": 1676027058.87354,
                "digitalSpecimen": {
                  "ods:physicalSpecimenId": "dissco-futures",
                  "ods:type": "BotanySpecimen",
                  "ods:attributes": {
                    "ods:physicalSpecimenIdType": "cetaf",
                    "ods:organizationId": "https://ror.org/0349vqz63",
                    "ods:specimenName": "dissco-futures",
                    "ods:datasetId": "A Dataset-5",
                    "ods:physicalSpecimenCollection": "A collection",
                    "ods:sourceSystemId": "20.5000.1025/GW0-TYL-YRU",
                    "dwca:id": "dissco-futures"
                  },
                  "ods:originalAttributes": {
                    "dwc:eventDate": "1994-08-25",
                    "dwc:family": "Cupressaceae",
                    "dwc:genus": "Glyptostrobus",
                    "dwc:higherGeography": "Indo-China",
                    "dwc:specificEpithet": "pensilis",
                    "dwc:nomenclaturalCode": "ICBN",
                    "dwc:catalogNumber": "00622948",
                    "dwc:country": "LA",
                    "dwc:basisOfRecord": "PreservedSpecimen"
                  }
                }
              },
              "change": [
                {
                  "op": "replace",
                  "path": "/ods:attributes/ods:datasetId",
                  "value": "A Dataset-5"
                }
              ],
              "comment": "Specimen has been updated"
            }
            """;
  }

}
