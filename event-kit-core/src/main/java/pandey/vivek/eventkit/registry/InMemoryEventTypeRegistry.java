package pandey.vivek.eventkit.registry;

import pandey.vivek.eventkit.registry.service.EventTypeRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEventTypeRegistry implements EventTypeRegistry {

	private final Map<String, Class<?>> types = new ConcurrentHashMap<>();

	public void register(String eventType, Class<?> clazz) {
		types.put(eventType, clazz);
	}

	@Override
	public Class<?> resolve(String eventType) {
		Class<?> clazz = types.get(eventType);
		if (clazz == null) {
			throw new IllegalStateException("Unknown event type: " + eventType);
		}
		return clazz;
	}

}