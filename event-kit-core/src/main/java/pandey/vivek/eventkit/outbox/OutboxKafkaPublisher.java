package pandey.vivek.eventkit.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import pandey.vivek.eventkit.outbox.repository.OutboxStore;
import pandey.vivek.eventkit.outbox.service.OutboxPublisher;

@RequiredArgsConstructor
@Slf4j
public class OutboxKafkaPublisher implements OutboxPublisher {

	private final OutboxStore outboxStore;

	private final KafkaTemplate<String, String> kafka;

	@Override
	@Transactional
	public void publishPending(int batchSize) {
		var events = outboxStore.lockPendingEvents(batchSize);
		for (var event : events) {
			try {
				var producerRecord = new ProducerRecord<>(event.getTopic(), event.getAggregateId(), event.getPayload());
				kafka.send(producerRecord).get();
				log.info("Published outbox event topic:{}, aggregateId:{}", event.getTopic(), event.getAggregateId());
				outboxStore.markPublished(event.getEventId());
			}
			catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				log.error("Interrupted while publishing topic:{}, aggregateId:{}", event.getTopic(),
						event.getAggregateId(), ex);
				outboxStore.markFailed(event.getEventId());
			}
			catch (Exception ex) {
				log.error("Error publishing topic:{}, aggregateId:{}", event.getTopic(), event.getAggregateId(), ex);
				outboxStore.markFailed(event.getEventId());
			}
		}
	}

}