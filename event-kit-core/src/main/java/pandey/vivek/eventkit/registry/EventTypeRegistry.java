package pandey.vivek.eventkit.registry;

public interface EventTypeRegistry {

	Class<?> resolve(String eventType);

}