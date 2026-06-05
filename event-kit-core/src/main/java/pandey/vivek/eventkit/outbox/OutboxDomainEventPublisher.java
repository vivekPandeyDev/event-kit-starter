package pandey.vivek.eventkit.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pandey.vivek.eventkit.api.DomainEvent;
import pandey.vivek.eventkit.api.DomainEventPublisher;
import pandey.vivek.eventkit.exception.EventPublishException;
import pandey.vivek.eventkit.outbox.entity.OutboxEvent;
import pandey.vivek.eventkit.outbox.repository.OutboxStore;
import pandey.vivek.eventkit.outbox.service.EventTypeResolver;
import pandey.vivek.eventkit.outbox.service.TopicResolver;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Slf4j
public class OutboxDomainEventPublisher implements DomainEventPublisher {

	private final OutboxStore outboxStore;

	private final ObjectMapper mapper;

	private final TopicResolver resolver;

	private final EventTypeResolver typeResolver;

	@Override
	public void publish(DomainEvent event) {
		try {
			String payload = mapper.writeValueAsString(event);
			String topic = resolver.resolve(event);
			String resolveName = typeResolver.resolve(event);
			if (log.isDebugEnabled()) {
				log.info("Payload for the topic: {}, resolved name: {} payload json: {}", topic, resolveName, payload);
			}
			var outbox = OutboxEvent.create(event.eventId(), event.aggregateId(), resolveName, topic, payload);
			outboxStore.save(outbox);
			if (log.isDebugEnabled()) {
				log.info("Saved outbox event with id:{} and event id: {}, outbox event: {}", outbox.getId(),
						outbox.getEventId(), outbox);
			}
		}
		catch (Exception ex) {
			log.info("Error while saving domain event with id: {} and aggregate id: {}, message: {}", event.eventId(),
					event.aggregateId(), ex.getMessage(), ex);
			throw new EventPublishException(ex);
		}
	}

}