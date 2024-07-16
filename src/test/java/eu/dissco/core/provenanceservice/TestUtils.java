package eu.dissco.core.provenanceservice;

import com.fasterxml.jackson.databind.ObjectMapper;
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

public class TestUtils {

  public static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
  public static final String PID = "https://hdl.handle.net/20.5000.1025/ABC-DEF-GHI/1";
  public static final String ACTIVITY_ID = "7ba628d4-2e28-4ce4-ad1e-e99c97c20507";
  public static final String SUBJECT_TYPE = "ods:DigitalSpecimen";

  public static CreateUpdateTombstoneEvent givenEvent() {
    return givenEvent(SUBJECT_TYPE);
  }

  public static CreateUpdateTombstoneEvent givenEvent(String entityType) {
    return new CreateUpdateTombstoneEvent()
        .withId(PID)
        .withType("ods:CreateUpdateTombstoneEvent")
        .withOdsID(PID)
        .withOdsType("https://doi.org/10.15468/1a2b3c")
        .withProvActivity(new ProvActivity()
            .withId(ACTIVITY_ID)
            .withType(ProvActivity.Type.ODS_CREATE)
            .withProvEndedAtTime(Date.from(Instant.parse("2024-06-11T09:14:00.348Z")))
            .withProvWasAssociatedWith(List.of(
                new ProvWasAssociatedWith()
                    .withId("https://orcid.org/0000-0002-1825-0097")
                    .withProvHadRole(ProvHadRole.ODS_APPROVER),
                new ProvWasAssociatedWith()
                    .withId("https://hdl.handle.net/20.5000.1025/XXX-XXX-XXX")
                    .withProvHadRole(ProvHadRole.ODS_REQUESTOR),
                new ProvWasAssociatedWith()
                    .withId("https://hdl.handle.net/20.5000.1025/XXX-XXX-XXX")
                    .withProvHadRole(ProvHadRole.ODS_GENERATOR)))
            .withProvUsed(PID))
        .withProvEntity(new ProvEntity()
            .withId(PID)
            .withType(entityType)
            .withProvWasGeneratedBy(ACTIVITY_ID))
        .withOdsHasProvAgent(List.of(
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

}
