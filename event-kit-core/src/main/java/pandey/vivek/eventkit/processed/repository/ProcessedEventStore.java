package pandey.vivek.eventkit.processed.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import pandey.vivek.eventkit.processed.entity.ProcessedEvent;
import pandey.vivek.eventkit.util.JdbcConverters;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
public class ProcessedEventStore {

	private static final String EVENT_ID = "eventId";

	private static final String CONSUMER = "consumer";

	private final NamedParameterJdbcTemplate jdbc;

	public void save(ProcessedEvent event) {

		var params = new MapSqlParameterSource().addValue(EVENT_ID, event.getEventId())
			.addValue(CONSUMER, event.getConsumer())
			.addValue("processedAt", JdbcConverters.toTimestamp(event.getProcessedAt()));

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
				""", params);
	}

	public Optional<ProcessedEvent> findById(UUID eventId, String consumer) {

		var results = jdbc.query("""
				select *
				from processed_event
				where event_id = :eventId
				  and consumer = :consumer
				""", Map.of(EVENT_ID, eventId, CONSUMER, consumer), rowMapper());

		return results.stream().findFirst();
	}

	public boolean exists(UUID eventId, String consumer) {

		Boolean exists = jdbc.queryForObject("""
				select exists(
				    select 1
				    from processed_event
				    where event_id = :eventId
				      and consumer = :consumer
				)
				""", Map.of(EVENT_ID, eventId, CONSUMER, consumer), Boolean.class);

		return Boolean.TRUE.equals(exists);
	}

	public void delete(UUID eventId, String consumer) {

		jdbc.update("""
				delete from processed_event
				where event_id = :eventId
				  and consumer = :consumer
				""", Map.of(EVENT_ID, eventId, CONSUMER, consumer));
	}

	private RowMapper<ProcessedEvent> rowMapper() {

		return (rs, rowNum) -> ProcessedEvent.restore(rs.getObject("event_id", UUID.class), rs.getString(CONSUMER),
				JdbcConverters.toInstant(rs.getTimestamp("processed_at")));
	}

}