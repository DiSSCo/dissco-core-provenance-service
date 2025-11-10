package eu.dissco.core.provenanceservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.provenanceservice.domain.CreateUpdateTombstoneRecord;
import eu.dissco.core.provenanceservice.exception.MongodbException;
import eu.dissco.core.provenanceservice.exception.UnknownSubjectException;
import eu.dissco.core.provenanceservice.repository.EventRepository;
import eu.dissco.core.provenanceservice.schema.CreateUpdateTombstoneEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessingService {

  private static final Map<String, String> SUBJECT_MAPPING = provideSubjectMapping();
  private final ObjectMapper mapper;
  private final EventRepository eventRepository;

  private static Map<String, String> provideSubjectMapping() {
    var map = new HashMap<String, String>();
    map.put("ods:DigitalSpecimen", "digital_specimen_provenance");
    map.put("ods:DigitalMedia", "digital_media_provenance");
    map.put("ods:Annotation", "annotation_provenance");
    map.put("ods:MachineAnnotationService", "machine_annotation_service_provenance");
    map.put("ods:DataMapping", "data_mapping_provenance");
    map.put("ods:SourceSystem", "source_system_provenance");
    map.put("ods:VirtualCollection", "virtual_collection_provenance");
    return map;
  }

  public void handleMessages(List<CreateUpdateTombstoneEvent> events)
      throws MongodbException {
    var provRecords = toMongodbRecords(events);
    if (!provRecords.isEmpty()) {
      var failedEvents = eventRepository.insertNewVersion(provRecords);
      if (failedEvents.isEmpty()) {
        log.info("Successfully processed {} events", events.size());
      } else {
        log.warn("Failed to insert event into mongodb collections: {}", failedEvents);
        throw new MongodbException();
      }
    }
  }

  private String parseSubjectType(CreateUpdateTombstoneEvent event) throws UnknownSubjectException {
    var collectionName = SUBJECT_MAPPING.get(event.getProvEntity().getType());
    if (collectionName == null) {
      log.error("Unknown subject type: {}", event.getProvEntity().getType());
      throw new UnknownSubjectException(
          "SubjectType: " + event.getProvEntity().getType() + " is unknown");
    } else {
      return collectionName;
    }
  }

  private List<CreateUpdateTombstoneRecord> toMongodbRecords(
      List<CreateUpdateTombstoneEvent> events) {
    return events.stream().map(event -> {
      try {
        var document = Document.parse(mapper.writeValueAsString(event));
        document.append("_id", event.getId());
        var filter = new Document("_id", event.getId());
        var collection = parseSubjectType(event);
        return new CreateUpdateTombstoneRecord(document, filter, collection);
      } catch (JsonProcessingException | UnknownSubjectException e) {
        log.error("Failed to parse event information from mongodb", e);
        return null;
      }
    }).filter(Objects::nonNull).toList();
  }

}
