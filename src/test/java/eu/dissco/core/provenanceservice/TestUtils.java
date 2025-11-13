package eu.dissco.core.provenanceservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.provenanceservice.domain.CreateUpdateTombstoneRecord;
import eu.dissco.core.provenanceservice.schema.Agent;
import eu.dissco.core.provenanceservice.schema.Agent.Type;
import eu.dissco.core.provenanceservice.schema.CreateUpdateTombstoneEvent;
import eu.dissco.core.provenanceservice.schema.ProvActivity;
import eu.dissco.core.provenanceservice.schema.ProvEntity;
import eu.dissco.core.provenanceservice.schema.ProvWasAssociatedWith;
import eu.dissco.core.provenanceservice.schema.ProvWasAssociatedWith.ProvHadRole;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.bson.Document;

public class TestUtils {

  public static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
  public static final String PID = "https://hdl.handle.net/20.5000.1025/ABC-DEF-GHI/1";
  public static final String ACTIVITY_ID = "7ba628d4-2e28-4ce4-ad1e-e99c97c20507";
  public static final String SUBJECT_TYPE = "ods:DigitalSpecimen";
  public static final String SPECIMEN_COL = "digital_specimen_provenance";
  public static final String MEDIA_COL = "digital_media_provenance";

  public static CreateUpdateTombstoneEvent givenEvent() {
    return givenEvent(SUBJECT_TYPE);
  }

  public static CreateUpdateTombstoneRecord givenProvRecord() throws JsonProcessingException {
    return givenProvRecord(givenEvent(), SPECIMEN_COL);
  }

  public static CreateUpdateTombstoneRecord givenProvRecord(CreateUpdateTombstoneEvent event, String collection)
      throws JsonProcessingException {
    var provDocument = Document.parse(MAPPER.writeValueAsString(event));
    provDocument.append("_id", event.getId());
    return new CreateUpdateTombstoneRecord(
        provDocument,
        new Document("_id", event.getId()),
        collection,
        event
    );
  }

  public static CreateUpdateTombstoneEvent givenEvent(String entityType) {
    return new CreateUpdateTombstoneEvent()
        .withId(PID)
        .withType("ods:CreateUpdateTombstoneEvent")
        .withDctermsIdentifier(PID)
        .withOdsFdoType("https://doi.org/10.15468/1a2b3c")
        .withProvActivity(new ProvActivity()
            .withId(ACTIVITY_ID)
            .withType(ProvActivity.Type.ODS_CREATE)
            .withProvEndedAtTime(Date.from(Instant.parse("2024-06-11T09:14:00.348Z")))
            .withProvWasAssociatedWith(List.of(
                new ProvWasAssociatedWith()
                    .withId("https://orcid.org/0000-0002-1825-0097")
                    .withProvHadRole(ProvHadRole.APPROVER),
                new ProvWasAssociatedWith()
                    .withId("https://hdl.handle.net/20.5000.1025/XXX-XXX-XXX")
                    .withProvHadRole(ProvHadRole.REQUESTOR),
                new ProvWasAssociatedWith()
                    .withId("https://hdl.handle.net/20.5000.1025/XXX-XXX-XXX")
                    .withProvHadRole(ProvHadRole.GENERATOR)))
            .withProvUsed(PID))
        .withProvEntity(new ProvEntity()
            .withId(PID)
            .withType(entityType)
            .withProvWasGeneratedBy(ACTIVITY_ID))
        .withOdsHasAgents(List.of(
            new Agent()
                .withType(Type.PROV_PERSON)
                .withId("https://orcid.org/0000-0002-1825-0097")
                .withSchemaName("John Doe"),
            new Agent()
                .withType(Type.PROV_SOFTWARE_AGENT)
                .withId("https://hdl.handle.net/20.5000.1025/XXX-XXX-XXX")
                .withSchemaName("Digital Specimen Processor")
        ));
  }

  public static String givenMessage() {
    return givenMessage("ods:DigitalSpecimen");
  }

  public static String givenMessage(String entityType) {
    return """
              {
        "@id": "https://hdl.handle.net/20.5000.1025/ABC-DEF-GHI/1",
        "@type": "ods:CreateUpdateTombstoneEvent",
        "dcterms:identifier": "https://hdl.handle.net/20.5000.1025/ABC-DEF-GHI/1",
        "ods:fdoType": "https://doi.org/10.15468/1a2b3c",
        "prov:Activity": {
          "@id": "7ba628d4-2e28-4ce4-ad1e-e99c97c20507",
          "@type": "ods:Create",
          "prov:wasAssociatedWith": [
            {
              "@id": "https://orcid.org/0000-0002-1825-0097",
              "prov:hadRole": "Approver"
            },
            {
              "@id": "https://hdl.handle.net/20.5000.1025/XXX-XXX-XXX",
              "prov:hadRole": "Requestor"
            },
            {
              "@id": "https://hdl.handle.net/20.5000.1025/XXX-XXX-XXX",
              "prov:hadRole": "Generator"
            }
          ],
          "prov:endedAtTime": "2024-06-11T09:14:00.348Z",
          "prov:used": "https://hdl.handle.net/20.5000.1025/ABC-DEF-GHI/1"
        },
        "prov:Entity": {
          "@id": "https://hdl.handle.net/20.5000.1025/ABC-DEF-GHI/1",
        """ +
        "\"@type\":\"" + entityType + "\"," +
        """
                "prov:wasGeneratedBy": "7ba628d4-2e28-4ce4-ad1e-e99c97c20507"
              },
              "ods:hasAgents": [
                {
                  "@id": "https://orcid.org/0000-0002-1825-0097",
                  "@type": "prov:Person",
                  "schema:name": "John Doe"
                },
                {
                  "@id": "https://hdl.handle.net/20.5000.1025/XXX-XXX-XXX",
                  "@type": "prov:SoftwareAgent",
                  "schema:name": "Digital Specimen Processor"
                }
              ]
            }
            """;
  }

}
