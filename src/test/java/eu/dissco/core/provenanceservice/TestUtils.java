package eu.dissco.core.provenanceservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.provenanceservice.domain.CreateUpdateDeleteEvent;
import java.time.Instant;
import java.util.UUID;

public class TestUtils {

  public static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
  public static final String PID = "20.5000.1025/MKA-93P-4MS/7";
  public static final UUID EVENT_ID = UUID.fromString("82adfa56-b1ad-46ee-b05c-e7bbdaa52fb1");
  public static final String TYPE = "update";
  public static final String AGENT = "processing-service";
  public static final String SUBJECT = "20.5000.1025/MKA-93P-4MS";
  public static final String SUBJECT_TYPE = "DigitalSpecimen";
  public static final Instant TIMESTAMP = Instant.parse("2023-02-10T08:23:51.817Z");
  public static final String COMMENT = "Specimen has been updated";

  public static CreateUpdateDeleteEvent givenEvent() throws JsonProcessingException {
    return givenEvent(SUBJECT_TYPE);
  }

  public static CreateUpdateDeleteEvent givenEvent(String subjectType) throws JsonProcessingException {
    return new CreateUpdateDeleteEvent(
        EVENT_ID,
        TYPE,
        AGENT,
        SUBJECT,
        subjectType,
        TIMESTAMP,
        MAPPER.readValue(DIGITAL_SPECIMEN, JsonNode.class),
        MAPPER.readValue(CHANGE, JsonNode.class),
        COMMENT);
  }
  private static String CHANGE = """
    [
    {
      "op": "replace",
      "path": "/ods:attributes/ods:datasetId",
      "value": "A Dataset-5"
    }
  ]
  """;


  private static String DIGITAL_SPECIMEN = """
      {
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
        }
      """;

}
