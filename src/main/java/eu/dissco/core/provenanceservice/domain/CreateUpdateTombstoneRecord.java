package eu.dissco.core.provenanceservice.domain;

import eu.dissco.core.provenanceservice.schema.CreateUpdateTombstoneEvent;
import org.bson.Document;

public record CreateUpdateTombstoneRecord(
    Document document,
    Document filter,
    String collection,
    CreateUpdateTombstoneEvent event
) {

}
