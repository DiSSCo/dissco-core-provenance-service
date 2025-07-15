package eu.dissco.core.provenanceservice.service;

import static eu.dissco.core.provenanceservice.TestUtils.MAPPER;
import static eu.dissco.core.provenanceservice.TestUtils.PID;
import static eu.dissco.core.provenanceservice.TestUtils.givenEvent;
import static eu.dissco.core.provenanceservice.TestUtils.givenMessage;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.dissco.core.provenanceservice.exception.MongodbException;
import eu.dissco.core.provenanceservice.exception.UnknownSubjectException;
import eu.dissco.core.provenanceservice.repository.EventRepository;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessingServiceTest {

  @Mock
  private EventRepository repository;

  private ProcessingService service;

  private static Stream<Arguments> provideTestHandleMessage() {
    return Stream.of(
        Arguments.of("ods:DigitalSpecimen", "digital_specimen_provenance"),
        Arguments.of("ods:DigitalMedia", "digital_media_provenance"),
        Arguments.of("ods:Annotation", "annotation_provenance"),
        Arguments.of("ods:VirtualCollection", "virtual_collection_provenance")
    );
  }

  @BeforeEach
  void setup() {
    service = new ProcessingService(MAPPER, repository);
  }

  @ParameterizedTest
  @MethodSource("provideTestHandleMessage")
  void testHandleMessage(String subjectType, String collectionName)
      throws JsonProcessingException, MongodbException, UnknownSubjectException {
    // Given
    var message = givenMessage(subjectType);
    var event = givenEvent(subjectType);
    given(repository.insertNewVersion(PID, event, collectionName)).willReturn(true);

    // When
    service.handleMessage(message);

    // Then
    then(repository).should().insertNewVersion(PID, event, collectionName);

  }

  @Test
  void testFailedHandleMessage() throws JsonProcessingException {
    // Given
    var message = givenMessage();
    var event = givenEvent();
    given(repository.insertNewVersion(PID, event, "digital_specimen_provenance")).willReturn(false);

    // When
    assertThatThrownBy(() -> service.handleMessage(message)).isInstanceOf(MongodbException.class);

    // Then
  }

  @Test
  void testHandleMessageUnknownSubjectType() {
    // Given
    var message = givenMessage("ods:UnknownType");

    // When
    assertThatThrownBy(() -> service.handleMessage(message)).isInstanceOf(
        UnknownSubjectException.class);

    // Then
  }



}
