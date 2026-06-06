package pandey.vivek.eventkit.outbox.entity;

import lombok.*;
import pandey.vivek.eventkit.outbox.enums.OutboxStatus;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "payload")
public class OutboxEvent {

	private UUID eventId;

	private String aggregateId;

	private String topic;

	private String payload;

	private OutboxStatus status;

	private Integer retryCount;

	private Instant createdAt;

	private Instant publishedAt;

	public static OutboxEvent create(UUID eventId, String aggregateId, String topic, String payload) {

		return OutboxEvent.builder()
			.eventId(eventId)
			.aggregateId(aggregateId)
			.topic(topic)
			.payload(payload)
			.status(OutboxStatus.PENDING)
			.retryCount(0)
			.createdAt(Instant.now())
			.build();
	}

}