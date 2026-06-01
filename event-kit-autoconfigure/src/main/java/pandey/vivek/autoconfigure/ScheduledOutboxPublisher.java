package pandey.vivek.autoconfigure;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import pandey.vivek.eventkit.outbox.OutboxKafkaPublisher;

@RequiredArgsConstructor
public class ScheduledOutboxPublisher {

	private final OutboxKafkaPublisher publisher;

	private final EventKitProperties props;

	@Scheduled(fixedDelayString = "${event-kit.outbox.publish-delay-ms:5000}")
	public void publish() {
		publisher.publishPending(props.getOutbox().getBatchSize());
	}

}