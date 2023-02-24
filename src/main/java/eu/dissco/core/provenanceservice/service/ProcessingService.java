package eu.dissco.core.provenanceservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.provenanceservice.domain.CreateUpdateDeleteEvent;
import eu.dissco.core.provenanceservice.exception.MongodbException;
import eu.dissco.core.provenanceservice.exception.UnknownSubjectException;
import eu.dissco.core.provenanceservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessingService {

  private final ObjectMapper mapper;
  private final EventRepository eventRepository;

  public void handleMessage(String message)
      throws JsonProcessingException, MongodbException, UnknownSubjectException {
    var event = mapper.readValue(message, CreateUpdateDeleteEvent.class);
    var versionId = generateUniqueVersionId(event.eventRecord());
    var collectionName = parseSubjectType(event.subjectType());
    var eventResult = eventRepository.insertNewVersion(versionId, event, collectionName);
    if (eventResult) {
      log.info("Successfully processed event information for event: {}", versionId);
    } else {
      log.warn("Failed to insert event into mongodb: {}", message);
      throw new MongodbException(message);
    }
  }

  private String parseSubjectType(String subjectType) throws UnknownSubjectException {
    return switch (subjectType) {
      case "DigitalSpecimen" -> "digital_specimen_provenance";
      case "DigitalMediaObject" -> "digital_media_provenance";
      case "Annotation" -> "annotation_provenance";
      default -> throw new UnknownSubjectException("SubjectType: " + subjectType + " is unknown");
    };
  }

  private String generateUniqueVersionId(JsonNode eventRecord) {
    return eventRecord.get("id").asText() + "/" + eventRecord.get("version").asInt();
  }

}
