package eu.dissco.core.provenanceservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.provenanceservice.domain.CreateUpdateDeleteEvent;
import eu.dissco.core.provenanceservice.exception.MongodbException;
import eu.dissco.core.provenanceservice.exception.UnknownSubjectException;
import eu.dissco.core.provenanceservice.repository.EventRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessingService {

  private final ObjectMapper mapper;
  private final EventRepository eventRepository;
  private static final Map<String, String> SUBJECT_MAPPING = provideSubjectMapping();

  private static Map<String, String> provideSubjectMapping() {
    var map = new HashMap<String, String>();
    map.put("DigitalSpecimen", "digital_specimen_provenance");
    map.put("DigitalMediaObject", "digital_media_provenance");
    map.put("Annotation", "annotation_provenance");
    map.put("MachineAnnotationService", "machine_annotation_service_provenance");
    map.put("Mapping", "mapping_provenance");
    map.put("SourceSystem", "source_system_provenance");
    return map;
  }

  public void handleMessage(String message)
      throws JsonProcessingException, MongodbException, UnknownSubjectException {
    var event = mapper.readValue(message, CreateUpdateDeleteEvent.class);
    var versionId = generateUniqueVersionId(event.eventRecord());
    var collectionName = parseSubjectType(event.subjectType());
    var eventResult = eventRepository.insertNewVersion(versionId, event, collectionName);
    if (eventResult) {
      log.info("Successfully processed event information for {}: {}", event.subjectType(),
          versionId);
    } else {
      log.warn("Failed to insert event into mongodb: {}", message);
      throw new MongodbException(message);
    }
  }

  private String parseSubjectType(String subjectType) throws UnknownSubjectException {
    var collectionName = SUBJECT_MAPPING.get(subjectType);
    if (collectionName == null){
      throw new UnknownSubjectException("SubjectType: " + subjectType + " is unknown");
    } else {
      return collectionName;
    }
  }

  private String generateUniqueVersionId(JsonNode eventRecord) {
    return eventRecord.get("id").asText() + "/" + eventRecord.get("version").asInt();
  }

}
