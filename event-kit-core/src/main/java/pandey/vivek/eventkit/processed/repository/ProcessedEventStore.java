package pandey.vivek.eventkit.processed.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import pandey.vivek.eventkit.processed.entity.ProcessedEvent;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
public class ProcessedEventStore {

	public static final String EVENT_ID = "event_id";

	public static final String CONSUMER = "consumer";

	private final NamedParameterJdbcTemplate jdbc;

	public void save(ProcessedEvent event) {

		jdbc.update("""
				insert into processed_event(
				    event_id,
				    consumer,
				    processed_at
				)
				values (
				    :eventId,
				    :consumer,
				    :processedAt
				)
				""", new BeanPropertySqlParameterSource(event));
	}

	public Optional<ProcessedEvent> findById(UUID eventId, String consumer) {

		var results = jdbc.query("""
				select *
				from processed_event
				where event_id = :eventId
				  and consumer = :consumer
				""", Map.of(EVENT_ID, eventId, CONSUMER, consumer),
				(rs, rowNum) -> ProcessedEvent.restore(UUID.fromString(rs.getString("event_id")),
						rs.getString(CONSUMER), rs.getTimestamp("processed_at").toInstant()));

		return results.stream().findFirst();
	}

	public boolean exists(UUID eventId, String consumer) {

		Integer count = jdbc.queryForObject("""
				select count(*)
				from processed_event
				where event_id = :eventId
				  and consumer = :consumer
				""", Map.of(EVENT_ID, eventId, CONSUMER, consumer), Integer.class);

		return count != null && count > 0;
	}

	public void delete(UUID eventId, String consumer) {

		jdbc.update("""
				delete from processed_event
				where event_id = :eventId
				  and consumer = :consumer
				""", Map.of(EVENT_ID, eventId, CONSUMER, consumer));
	}

}