package pandey.vivek.eventkit.outbox.service;

public interface OutboxPublisher {

	void publishPending(int batchSize);

}
