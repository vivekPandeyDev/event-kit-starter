package pandey.vivek.eventkit.processed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import pandey.vivek.eventkit.processed.entity.ProcessedEvent;
import pandey.vivek.eventkit.processed.repository.ProcessedEventStore;
import pandey.vivek.eventkit.processed.service.EventDeduplicator;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class JpaEventDeduplicator implements EventDeduplicator {

	private final ProcessedEventStore processedEventStore;

	@Override
	@Transactional
	public boolean firstProcessing(UUID eventId, String consumer) {

		try {
			processedEventStore.save(ProcessedEvent.create(eventId, consumer));
			log.info("Saving Event as processed event with event id: {} and consumer: {}", eventId, consumer);
			return true;

		}
		catch (DataIntegrityViolationException ex) {
			log.error("Error while Saving Event as processed event with event id: {} and consumer: {}, message: {}",
					eventId, consumer, ex.getMessage(), ex);
			return false;
		}
	}

}