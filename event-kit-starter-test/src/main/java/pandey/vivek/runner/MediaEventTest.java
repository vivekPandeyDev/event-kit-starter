package pandey.vivek.runner;

import pandey.vivek.eventkit.annotation.EventTopic;
import pandey.vivek.eventkit.api.DomainEvent;

import java.util.UUID;

@EventTopic("media-events")
public record MediaEventTest(UUID eventId, UUID mediaId) implements DomainEvent {

    @Override
    public String aggregateId() {
        return mediaId.toString();
    }
}