package pandey.vivek.autoconfigure;

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
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableConfigurationProperties(EventKitProperties.class)
@EntityScan(basePackageClasses = OutboxEvent.class)
@EnableJpaRepositories(basePackageClasses = OutboxRepository.class)
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
			TopicResolver resolver) {
		return new OutboxDomainEventPublisher(repo, mapper, resolver);
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

}