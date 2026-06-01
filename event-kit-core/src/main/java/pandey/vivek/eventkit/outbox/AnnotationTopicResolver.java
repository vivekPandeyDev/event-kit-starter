package pandey.vivek.eventkit.outbox;

import pandey.vivek.eventkit.annotation.EventTopic;
import pandey.vivek.eventkit.api.DomainEvent;
import pandey.vivek.eventkit.outbox.service.TopicResolver;

public class AnnotationTopicResolver implements TopicResolver {

	@Override
	public String resolve(DomainEvent event) {
		var annotation = event.getClass().getAnnotation(EventTopic.class);
		if (annotation == null) {
			throw new IllegalStateException("Missing @EventTopic on " + event.getClass());
		}
		return annotation.value();
	}

}