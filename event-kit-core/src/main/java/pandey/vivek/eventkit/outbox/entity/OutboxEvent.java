package pandey.vivek.eventkit.outbox.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pandey.vivek.eventkit.outbox.enums.OutboxStatus;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "payload")
public class OutboxEvent {

	private UUID id;

	private UUID eventId;

	private String aggregateId;

	private String eventType;

	private String topic;

	private String payload;

	private OutboxStatus status;

	private Integer retryCount;

	private Instant createdAt;

	private Instant publishedAt;

	public static OutboxEvent create(UUID eventId, String aggregateId, String eventType, String topic, String payload) {

		OutboxEvent e = new OutboxEvent();
		e.id = UUID.randomUUID();
		e.eventId = eventId;
		e.aggregateId = aggregateId;
		e.eventType = eventType;
		e.topic = topic;
		e.payload = payload;
		e.status = OutboxStatus.PENDING;
		e.retryCount = 0;
		e.createdAt = Instant.now();
		return e;
	}

	public void markPublished() {
		this.status = OutboxStatus.PUBLISHED;
		this.publishedAt = Instant.now();
	}

	public void markFailed() {
		this.retryCount++;

		if (this.retryCount >= 5) {
			this.status = OutboxStatus.FAILED;
		}
	}

	// Needed when reading from DB
	public static OutboxEvent restore(UUID id, UUID eventId, String aggregateId, String eventType, String topic,
			String payload, OutboxStatus status, Integer retryCount, Instant createdAt, Instant publishedAt) {

		OutboxEvent e = new OutboxEvent();
		e.id = id;
		e.eventId = eventId;
		e.aggregateId = aggregateId;
		e.eventType = eventType;
		e.topic = topic;
		e.payload = payload;
		e.status = status;
		e.retryCount = retryCount;
		e.createdAt = createdAt;
		e.publishedAt = publishedAt;
		return e;
	}

}