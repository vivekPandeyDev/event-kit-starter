package pandey.vivek.eventkit.registry;

import pandey.vivek.eventkit.api.DomainEvent;

public interface EventTypeResolver {

	String resolve(DomainEvent event);

}