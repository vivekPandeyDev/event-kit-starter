package pandey.vivek.eventkit.registry.service;

public interface EventTypeRegistry {

	Class<?> resolve(String eventType);

}