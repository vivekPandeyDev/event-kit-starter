package pandey.vivek.eventkit.processed.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProcessedEvent {

	@Id
	private UUID eventId;

	private String consumer;

	private Instant processedAt;

	public static ProcessedEvent create(UUID eventId, String consumer) {
		return new ProcessedEvent(eventId, consumer, Instant.now());
	}

}