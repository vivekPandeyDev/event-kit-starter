package pandey.vivek.eventkit.processed.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcessedEvent {

	private UUID eventId;

	private String consumer;

	private Instant processedAt;

	public static ProcessedEvent create(UUID eventId, String consumer) {
		return new ProcessedEvent(eventId, consumer, Instant.now());
	}

	public static ProcessedEvent restore(UUID eventId, String consumer, Instant processedAt) {
		return new ProcessedEvent(eventId, consumer, processedAt);
	}

}