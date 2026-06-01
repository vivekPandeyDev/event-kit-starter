package pandey.vivek.eventkit.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pandey.vivek.eventkit.api.DomainEvent;
import pandey.vivek.eventkit.api.DomainEventPublisher;
import pandey.vivek.eventkit.exception.EventPublishException;
import pandey.vivek.eventkit.outbox.entity.OutboxEvent;
import pandey.vivek.eventkit.outbox.repository.OutboxRepository;
import pandey.vivek.eventkit.outbox.service.TopicResolver;

@RequiredArgsConstructor
@Slf4j
public class OutboxDomainEventPublisher implements DomainEventPublisher {

    private final OutboxRepository repo;
    private final ObjectMapper mapper;
    private final TopicResolver resolver;

    @Override
    public void publish(DomainEvent event) {
        try {
            String payload = mapper.writeValueAsString(event);
            String topic = resolver.resolve(event);
            if (log.isDebugEnabled()) {
                log.info("Payload for the topic: {}, payload json: {}", topic, payload);
            }
            var outbox = OutboxEvent.create(event.eventId(), event.aggregateId(), event.getClass().getName(), topic, payload);
            repo.save(outbox);
            if (log.isDebugEnabled()) {
                log.info("Saved outbox event with id:{} and event id: {}, outbox event: {}", outbox.getId(), outbox.getEventId(), outbox);
            }
        } catch (Exception ex) {
            log.info("Error while saving domain event with id: {} and aggregate id: {}, message: {}", event.eventId(), event.aggregateId(), ex.getMessage(), ex);
            throw new EventPublishException(ex);
        }
    }
}