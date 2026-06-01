package pandey.vivek.eventkit.outbox.service;

import pandey.vivek.eventkit.api.DomainEvent;

public interface TopicResolver {

	String resolve(DomainEvent event);

}