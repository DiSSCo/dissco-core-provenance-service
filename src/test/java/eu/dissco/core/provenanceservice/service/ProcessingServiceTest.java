package eu.dissco.core.provenanceservice.service;

import static eu.dissco.core.provenanceservice.TestUtils.MAPPER;
import static eu.dissco.core.provenanceservice.TestUtils.PID;
import static eu.dissco.core.provenanceservice.TestUtils.givenEvent;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;


import com.fasterxml.jackson.core.JsonProcessingException;
import eu.dissco.core.provenanceservice.exception.MongodbException;
import eu.dissco.core.provenanceservice.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessingServiceTest {

  @Mock
  private EventRepository repository;

  private ProcessingService service;

  @BeforeEach
  void setup(){
    service = new ProcessingService(MAPPER, repository);
  }

  @Test
  void testHandleMessage() throws JsonProcessingException, MongodbException {
    // Given
    var message = givenMessage();
    var event = givenEvent();
    given(repository.insertNewVersion(PID, event)).willReturn(true);

    // When
    service.handleMessage(message);

    // Then
  }

  @Test
  void testFailedHandleMessage() throws JsonProcessingException {
    // Given
    var message = givenMessage();
    var event = givenEvent();
    given(repository.insertNewVersion(PID, event)).willReturn(false);

    // When
    assertThatThrownBy(() -> service.handleMessage(message)).isInstanceOf(MongodbException.class);

    // Then
  }

  private String givenMessage() {
    return """
        {
          "id": "82adfa56-b1ad-46ee-b05c-e7bbdaa52fb1",
          "eventType": "update",
          "agent": "processing-service",
          "subject": "20.5000.1025/MKA-93P-4MS",
          "timestamp": "2023-02-10T08:23:51.817Z",
          "digitalSpecimenRecord": {
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
