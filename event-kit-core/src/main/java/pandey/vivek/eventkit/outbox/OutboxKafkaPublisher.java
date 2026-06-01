package pandey.vivek.eventkit.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import pandey.vivek.eventkit.outbox.repository.OutboxRepository;
import pandey.vivek.eventkit.outbox.service.OutboxPublisher;

@RequiredArgsConstructor
@Slf4j
public class OutboxKafkaPublisher implements OutboxPublisher {

	private final OutboxRepository repo;

	private final KafkaTemplate<String, String> kafka;

	public void publishPending(int batchSize) {
		var events = repo.lockPendingEvents(50);
		for (var event : events) {
			try {
				kafka.send(event.getTopic(), event.getAggregateId(), event.getPayload());
				log.info("publishing outbox event to topic: {}, with aggregate-id: {}", event.getTopic(),
						event.getAggregateId());
				event.markPublished();
				repo.save(event);
			}
			catch (Exception ex) {
				log.error("Error while publishing outbox event to topic: {}, with aggregate-id: {}, message: {}",
						event.getTopic(), event.getAggregateId(), ex.getMessage(), ex);
				event.markFailed();
				repo.save(event);
			}
		}

	}

}