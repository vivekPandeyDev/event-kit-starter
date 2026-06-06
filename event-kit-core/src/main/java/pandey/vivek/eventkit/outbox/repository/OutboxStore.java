package pandey.vivek.eventkit.outbox.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import pandey.vivek.eventkit.outbox.entity.OutboxEvent;
import pandey.vivek.eventkit.outbox.enums.OutboxStatus;
import pandey.vivek.eventkit.util.JdbcConverters;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class OutboxStore {

	public static final String EVENT_ID = "eventId";

	private final NamedParameterJdbcTemplate jdbc;

	@Transactional
	public void save(OutboxEvent event) {

		String sql = """
				insert into outbox_event(
				    event_id,
				    aggregate_id,
				    topic,
				    payload,
				    status,
				    retry_count,
				    created_at,
				    published_at
				)
				values (
				    :eventId,
				    :aggregateId,
				    :topic,
				    :payload,
				    :status,
				    :retryCount,
				    :createdAt,
				    :publishedAt
				)
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue(EVENT_ID, event.getEventId())
			.addValue("aggregateId", event.getAggregateId())
			.addValue("topic", event.getTopic())
			.addValue("payload", event.getPayload())
			.addValue("status", event.getStatus().name())
			.addValue("retryCount", event.getRetryCount())
			.addValue("createdAt", JdbcConverters.toTimestamp(event.getCreatedAt()))
			.addValue("publishedAt", JdbcConverters.toTimestamp(event.getPublishedAt()));

		jdbc.update(sql, params);
	}

	@Transactional
	public List<OutboxEvent> lockPendingEvents(int limit) {

		return jdbc.query("""
				select *
				from outbox_event
				where status = 'PENDING'
				order by created_at
				for update skip locked
				limit :limit
				""", Map.of("limit", limit), rowMapper());
	}

	@Transactional
	public void markPublished(UUID eventId) {

		jdbc.update("""
				update outbox_event
				set status = 'PUBLISHED',
				    published_at = now()
				where event_id = :eventId
				""", Map.of(EVENT_ID, eventId));
	}

	@Transactional
	public void markFailed(UUID eventId) {

		jdbc.update("""
				update outbox_event
				set retry_count = retry_count + 1,
				    status = case
				        when retry_count + 1 >= 5 then 'FAILED'
				        else status
				    end
				where event_id = :eventId
				""", Map.of(EVENT_ID, eventId));
	}

	private RowMapper<OutboxEvent> rowMapper() {

		return (rs, rowNum) -> OutboxEvent.builder()
			.eventId(rs.getObject("event_id", UUID.class))
			.aggregateId(rs.getString("aggregate_id"))
			.topic(rs.getString("topic"))
			.payload(rs.getString("payload"))
			.status(OutboxStatus.valueOf(rs.getString("status")))
			.retryCount(rs.getInt("retry_count"))
			.createdAt(JdbcConverters.toInstant(rs.getTimestamp("created_at")))
			.publishedAt(JdbcConverters.toInstant(rs.getTimestamp("published_at")))
			.build();
	}

}