package pandey.vivek.autoconfigure;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import pandey.vivek.eventkit.api.DomainEventPublisher;
import pandey.vivek.eventkit.outbox.AnnotationTopicResolver;
import pandey.vivek.eventkit.outbox.OutboxDomainEventPublisher;
import pandey.vivek.eventkit.outbox.OutboxKafkaPublisher;
import pandey.vivek.eventkit.outbox.repository.OutboxStore;
import pandey.vivek.eventkit.outbox.service.OutboxPublisher;
import pandey.vivek.eventkit.outbox.service.TopicResolver;
import pandey.vivek.eventkit.processed.JpaEventDeduplicate;
import pandey.vivek.eventkit.processed.repository.ProcessedEventStore;
import pandey.vivek.eventkit.processed.service.EventDeduplicate;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(EventKitProperties.class)
@EnableScheduling
public class EventKitAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public TopicResolver topicResolver() {
		return new AnnotationTopicResolver();
	}

	@Bean
	@ConditionalOnMissingBean
	public OutboxStore outboxStore(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		return new OutboxStore(namedParameterJdbcTemplate);
	}

	@Bean
	@ConditionalOnMissingBean
	public ProcessedEventStore processedEventStore(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		return new ProcessedEventStore(namedParameterJdbcTemplate);
	}

	@Bean
	@ConditionalOnMissingBean
	public DomainEventPublisher domainEventPublisher(OutboxStore repo, ObjectMapper mapper,
			TopicResolver topicResolver) {
		return new OutboxDomainEventPublisher(repo, mapper, topicResolver);
	}

	@Bean
	@ConditionalOnMissingBean
	public OutboxPublisher outboxKafkaPublisher(OutboxStore repo, KafkaTemplate<String, String> kafka) {
		return new OutboxKafkaPublisher(repo, kafka);
	}

	@Bean
	@ConditionalOnMissingBean
	@DependsOn("eventKitSchemaInitializer")
	public ScheduledOutboxPublisher scheduledOutboxPublisher(OutboxKafkaPublisher publisher, EventKitProperties props) {
		return new ScheduledOutboxPublisher(publisher, props);
	}

	@Bean
	@ConditionalOnMissingBean
	public EventDeduplicate eventDeduplicate(ProcessedEventStore processedEventStore) {
		return new JpaEventDeduplicate(processedEventStore);
	}

	@Bean
	@ConditionalOnBean(DataSource.class)
	InitializingBean eventKitSchemaInitializer(DataSource dataSource) {

		return () -> {
			var populate = new ResourceDatabasePopulator(new ClassPathResource("eventkit-schema.sql"));
			DatabasePopulatorUtils.execute(populate, dataSource);
		};
	}

}