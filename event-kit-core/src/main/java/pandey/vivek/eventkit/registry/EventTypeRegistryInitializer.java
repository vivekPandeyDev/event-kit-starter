package pandey.vivek.eventkit.registry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import pandey.vivek.eventkit.annotation.EventType;

@RequiredArgsConstructor
@Slf4j
public class EventTypeRegistryInitializer implements InitializingBean {

	private final BeanFactory beanFactory;

	private final InMemoryEventTypeRegistry registry;

	@Override
	public void afterPropertiesSet() throws ClassNotFoundException {
		log.info("Event handler registration using annotation");
		var scanner = new ClassPathScanningCandidateComponentProvider(false);

		scanner.addIncludeFilter(new AnnotationTypeFilter(EventType.class));

		var basePackages = AutoConfigurationPackages.get(beanFactory);

		for (String basePackage : basePackages) {

			var candidates = scanner.findCandidateComponents(basePackage);

			for (var candidate : candidates) {

				Class<?> clazz = Class.forName(candidate.getBeanClassName());

				EventType type = clazz.getAnnotation(EventType.class);

				registry.register(type.value(), clazz);
			}
		}
	}

}