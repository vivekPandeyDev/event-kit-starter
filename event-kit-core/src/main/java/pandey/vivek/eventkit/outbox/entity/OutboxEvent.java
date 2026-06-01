package pandey.vivek.eventkit.outbox.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pandey.vivek.eventkit.outbox.enums.OutboxStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"payload"})
public class OutboxEvent {

    @Id
    private UUID id;

    private UUID eventId;

    private String aggregateId;

    private String eventType;

    private String topic;

    @Lob
    private String payload;

    @Enumerated(EnumType.STRING)
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
        status = OutboxStatus.PUBLISHED;
        publishedAt = Instant.now();
    }

    public void markFailed() {
        retryCount++;
        if (retryCount >= 5) {
            status = OutboxStatus.FAILED;
        }
    }
}