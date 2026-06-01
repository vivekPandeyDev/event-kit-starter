package pandey.vivek.eventkit.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import pandey.vivek.eventkit.outbox.repository.OutboxRepository;
import pandey.vivek.eventkit.outbox.service.OutboxPublisher;

@RequiredArgsConstructor
@Slf4j
public class OutboxKafkaPublisher implements OutboxPublisher {

	private final OutboxRepository repo;

	private final KafkaTemplate<String, String> kafka;

	@Override
	@Transactional
	public void publishPending(int batchSize) {
		var events = repo.lockPendingEvents(batchSize);
		for (var event : events) {
			try {
				var producerRecord = new ProducerRecord<>(event.getTopic(), event.getAggregateId(), event.getPayload());
				producerRecord.headers().add("eventType", event.getEventType().getBytes());
				kafka.send(producerRecord).get();
				log.info("Published outbox event topic:{}, aggregateId:{}, eventType:{}", event.getTopic(),
						event.getAggregateId(), event.getEventType());
				event.markPublished();
			}
			catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				log.error("Interrupted while publishing topic:{}, aggregateId:{}", event.getTopic(),
						event.getAggregateId(), ex);
				event.markFailed();
			}
			catch (Exception ex) {
				log.error("Error publishing topic:{}, aggregateId:{}", event.getTopic(), event.getAggregateId(), ex);
				event.markFailed();
			}
		}
	}

}