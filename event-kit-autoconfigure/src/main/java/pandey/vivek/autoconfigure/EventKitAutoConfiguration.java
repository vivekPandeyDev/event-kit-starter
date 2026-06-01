package pandey.vivek.autoconfigure;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import pandey.vivek.eventkit.api.DomainEventPublisher;
import pandey.vivek.eventkit.outbox.AnnotationTopicResolver;
import pandey.vivek.eventkit.outbox.OutboxDomainEventPublisher;
import pandey.vivek.eventkit.outbox.OutboxKafkaPublisher;
import pandey.vivek.eventkit.outbox.entity.OutboxEvent;
import pandey.vivek.eventkit.outbox.repository.OutboxRepository;
import pandey.vivek.eventkit.outbox.service.OutboxPublisher;
import pandey.vivek.eventkit.outbox.service.TopicResolver;
import pandey.vivek.eventkit.processed.JpaEventDeduplicator;
import pandey.vivek.eventkit.processed.entity.ProcessedEvent;
import pandey.vivek.eventkit.processed.repository.ProcessedEventRepository;
import pandey.vivek.eventkit.processed.service.EventDeduplicator;
import pandey.vivek.eventkit.registry.AnnotationEventTypeResolver;
import pandey.vivek.eventkit.registry.EventTypeRegistryInitializer;
import pandey.vivek.eventkit.registry.InMemoryEventTypeRegistry;
import pandey.vivek.eventkit.registry.service.EventTypeRegistry;
import pandey.vivek.eventkit.registry.service.EventTypeResolver;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableConfigurationProperties(EventKitProperties.class)
@EntityScan(basePackageClasses = { OutboxEvent.class, ProcessedEvent.class })
@EnableJpaRepositories(basePackageClasses = { OutboxRepository.class, ProcessedEventRepository.class })
@EnableScheduling
public class EventKitAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public TopicResolver topicResolver() {
		return new AnnotationTopicResolver();
	}

	@Bean
	@ConditionalOnMissingBean
	public DomainEventPublisher domainEventPublisher(OutboxRepository repo, ObjectMapper mapper,
			TopicResolver topicResolver, EventTypeResolver eventTypeResolver) {
		return new OutboxDomainEventPublisher(repo, mapper, topicResolver, eventTypeResolver);
	}

	@Bean
	@ConditionalOnMissingBean
	public OutboxPublisher outboxKafkaPublisher(OutboxRepository repo, KafkaTemplate<String, String> kafka) {
		return new OutboxKafkaPublisher(repo, kafka);
	}

	@Bean
	@ConditionalOnMissingBean
	public ScheduledOutboxPublisher scheduledOutboxPublisher(OutboxKafkaPublisher publisher, EventKitProperties props) {
		return new ScheduledOutboxPublisher(publisher, props);
	}

	@Bean
	@ConditionalOnMissingBean
	public EventDeduplicator eventDeduplicator(ProcessedEventRepository processedEventRepository) {
		return new JpaEventDeduplicator(processedEventRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public EventTypeResolver eventTypeResolver() {
		return new AnnotationEventTypeResolver();
	}

	@Bean
	@ConditionalOnMissingBean
	public EventTypeRegistry eventTypeRegistry() {
		return new InMemoryEventTypeRegistry();
	}

	@Bean
	@ConditionalOnMissingBean
	public EventTypeRegistryInitializer eventTypeRegistryInitializer(BeanFactory ctx,
			InMemoryEventTypeRegistry registry) {
		return new EventTypeRegistryInitializer(ctx, registry);
	}

}