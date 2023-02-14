package eu.dissco.core.provenanceservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.provenanceservice.domain.CreateUpdateDeleteEvent;
import eu.dissco.core.provenanceservice.exception.MongodbException;
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

  public void handleMessage(String message) throws JsonProcessingException, MongodbException {
    var event = mapper.readValue(message, CreateUpdateDeleteEvent.class);
    var versionId = generateUniqueVersionId(event.digitalSpecimenRecord());
    var eventResult = eventRepository.insertNewVersion(versionId, event);
    if (eventResult) {
      log.info("Successfully processed event information for event: {}", versionId);
    } else {
      log.warn("Failed to insert event into mongodb: {}", message);
      throw new MongodbException(message);
    }
  }

  private String generateUniqueVersionId(JsonNode digitalSpecimen) {
    return digitalSpecimen.get("id").asText() + "/" + digitalSpecimen.get("version").asInt();
  }

}
