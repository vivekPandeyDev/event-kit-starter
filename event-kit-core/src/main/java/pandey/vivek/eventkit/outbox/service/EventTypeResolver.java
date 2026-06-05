package pandey.vivek.eventkit.outbox.service;

import pandey.vivek.eventkit.api.DomainEvent;

public interface EventTypeResolver {

	String resolve(DomainEvent event);

}