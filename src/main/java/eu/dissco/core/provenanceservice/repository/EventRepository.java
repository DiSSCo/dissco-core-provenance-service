package eu.dissco.core.provenanceservice.repository;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import eu.dissco.core.provenanceservice.domain.CreateUpdateTombstoneRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventRepository {

  private final MongoDatabase database;

  public List<CreateUpdateTombstoneRecord> insertNewVersion(
      List<CreateUpdateTombstoneRecord> provRecords) {
    var failedEvents = new ArrayList<CreateUpdateTombstoneRecord>();
    var collectionMap = provRecords.stream().collect(Collectors.groupingBy(
        CreateUpdateTombstoneRecord::collection
    ));
    for (var entry : collectionMap.entrySet()) {
      var collection = database.getCollection(entry.getKey());
      var queryList = entry.getValue().stream().map(provRecord -> {
            var replaceOptions = new ReplaceOptions();
            replaceOptions.upsert(true);
            return new ReplaceOneModel<>(provRecord.filter(), provRecord.document(), replaceOptions);
          }
      ).toList();
      var result = collection.bulkWrite(queryList);
      if (!result.wasAcknowledged()) {
        failedEvents.addAll(entry.getValue());
      }
    }
    return failedEvents;
  }
}
