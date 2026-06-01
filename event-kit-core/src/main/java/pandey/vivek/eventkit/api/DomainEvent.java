package pandey.vivek.eventkit.api;

import java.util.UUID;

public interface DomainEvent {

    UUID eventId();

    String aggregateId();
}