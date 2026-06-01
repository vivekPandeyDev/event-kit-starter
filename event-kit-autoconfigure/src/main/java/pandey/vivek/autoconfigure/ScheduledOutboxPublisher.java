package pandey.vivek.autoconfigure;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import pandey.vivek.eventkit.outbox.OutboxKafkaPublisher;

@RequiredArgsConstructor
public class ScheduledOutboxPublisher {

	private final OutboxKafkaPublisher publisher;

	@Scheduled(fixedDelay = 5000)
	public void publish() {
		publisher.publishPending();
	}

}