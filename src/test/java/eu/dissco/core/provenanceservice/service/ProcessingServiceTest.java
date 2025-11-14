package eu.dissco.core.provenanceservice.service;

import static eu.dissco.core.provenanceservice.TestUtils.MAPPER;
import static eu.dissco.core.provenanceservice.TestUtils.MEDIA_COL;
import static eu.dissco.core.provenanceservice.TestUtils.SPECIMEN_COL;
import static eu.dissco.core.provenanceservice.TestUtils.givenEvent;
import static eu.dissco.core.provenanceservice.TestUtils.givenProvRecord;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.dissco.core.provenanceservice.repository.EventRepository;
import java.util.List;
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
  @Mock
  private RabbitMqPublisherService rabbitMqPublisherService;

  private ProcessingService service;

  private static Stream<Arguments> provideTestHandleMessages() {
    return Stream.of(
        Arguments.of("ods:DigitalSpecimen", SPECIMEN_COL),
        Arguments.of("ods:DigitalMedia", MEDIA_COL),
        Arguments.of("ods:Annotation", "annotation_provenance"),
        Arguments.of("ods:VirtualCollection", "virtual_collection_provenance")
    );
  }

  @BeforeEach
  void setup() {
    service = new ProcessingService(MAPPER, rabbitMqPublisherService, repository);
  }

  @ParameterizedTest
  @MethodSource("provideTestHandleMessages")
  void testHandleMessages(String subjectType, String collection)
      throws Exception {
    // Given
    var event = givenEvent(subjectType);
    given(repository.insertNewVersion(List.of(givenProvRecord(event, collection)))).willReturn(List.of());

    // When
    assertDoesNotThrow(() -> service.handleMessages(List.of(event)));
  }

  @Test
  void testFailedHandleMessages() throws JsonProcessingException {
    // Given
    var event = givenEvent();
    given(repository.insertNewVersion(List.of(givenProvRecord()))).willReturn(List.of(givenProvRecord()));

    // When
    service.handleMessages(List.of(event));

    // Then
    then(rabbitMqPublisherService).should().dlqMessage(event);
  }

  @Test
  void testHandleMessagesUnknownSubjectType() throws Exception {
    // Given
    var event = givenEvent("unknown subject");

    // When
    service.handleMessages(List.of(event));

    // Then
    then(repository).shouldHaveNoInteractions();
    then(rabbitMqPublisherService).should().dlqMessage(event);
  }

}
