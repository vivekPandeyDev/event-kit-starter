package pandey.vivek.eventkit.api;

public interface DomainEventPublisher {

	void publish(DomainEvent event);

}