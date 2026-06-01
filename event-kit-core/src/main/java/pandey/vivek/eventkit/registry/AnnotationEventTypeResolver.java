package pandey.vivek.eventkit.registry;

import pandey.vivek.eventkit.annotation.EventType;
import pandey.vivek.eventkit.api.DomainEvent;

public class AnnotationEventTypeResolver implements EventTypeResolver {

	@Override
	public String resolve(DomainEvent event) {

		EventType type = event.getClass().getAnnotation(EventType.class);

		if (type == null) {
			throw new IllegalStateException("Missing @EventType");
		}

		return type.value();
	}

}