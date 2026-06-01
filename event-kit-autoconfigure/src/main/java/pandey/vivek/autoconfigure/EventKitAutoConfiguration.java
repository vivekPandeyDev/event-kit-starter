package pandey.vivek.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pandey.vivek.eventkit.api.DomainEventPublisher;
import pandey.vivek.eventkit.outbox.AnnotationTopicResolver;
import pandey.vivek.eventkit.outbox.OutboxDomainEventPublisher;
import pandey.vivek.eventkit.outbox.repository.OutboxRepository;
import pandey.vivek.eventkit.outbox.service.TopicResolver;

@Configuration
@EnableConfigurationProperties(EventKitProperties.class)
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

}