package eu.dissco.core.provenanceservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.core.provenanceservice.exception.MongodbException;
import eu.dissco.core.provenanceservice.exception.UnknownSubjectException;
import eu.dissco.core.provenanceservice.repository.EventRepository;
import eu.dissco.core.provenanceservice.schema.CreateUpdateTombstoneEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  public void handleMessage(String message)
      throws JsonProcessingException, MongodbException, UnknownSubjectException {
    var event = mapper.readValue(message, CreateUpdateTombstoneEvent.class);
    var versionId = event.getId();
    var collectionName = parseSubjectType(event);
    var eventResult = eventRepository.insertNewVersion(versionId, event, collectionName);
    if (eventResult) {
      log.info("Successfully processed {} event information for {}: {}",
          event.getProvActivity().getType(),
          event.getProvEntity().getType(),
          versionId);
    } else {
      log.warn("Failed to insert event into mongodb: {}", message);
      throw new MongodbException(message);
    }
  }

  private String parseSubjectType(CreateUpdateTombstoneEvent event) throws UnknownSubjectException {
    var collectionName = SUBJECT_MAPPING.get(event.getProvEntity().getType());
    if (collectionName == null) {
      throw new UnknownSubjectException(
          "SubjectType: " + event.getProvEntity().getType() + " is unknown");
    } else {
      return collectionName;
    }
  }

}
