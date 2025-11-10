package eu.dissco.core.provenanceservice.domain;

import org.bson.Document;

public record CreateUpdateTombstoneRecord(
    Document document,
    Document filter,
    String collection
) {

}
