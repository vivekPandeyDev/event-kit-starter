package pandey.vivek.eventkit.registry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import pandey.vivek.eventkit.annotation.EventHandler;
import pandey.vivek.eventkit.annotation.EventType;
import pandey.vivek.eventkit.registry.service.EventTypeRegistry;

@RequiredArgsConstructor
@Slf4j
public class EventTypeRegistryInitializer implements InitializingBean {

	private final ApplicationContext context;

	private final InMemoryEventTypeRegistry registry;

	@Override
	public void afterPropertiesSet() {
		log.info("Event handler registration using annotation");
		var beans = context.getBeansWithAnnotation(EventHandler.class);
		beans.values().forEach(bean -> {
			Class<?> clazz = bean.getClass();
			EventType type = clazz.getAnnotation(EventType.class);
			if (type != null) {
				registry.register(type.value(), clazz);
			}
		});
	}

}