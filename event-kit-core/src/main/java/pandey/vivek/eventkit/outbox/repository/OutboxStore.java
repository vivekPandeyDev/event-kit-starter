package pandey.vivek.eventkit.outbox.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import pandey.vivek.eventkit.outbox.entity.OutboxEvent;
import pandey.vivek.eventkit.outbox.enums.OutboxStatus;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class OutboxStore {

	private final NamedParameterJdbcTemplate jdbc;

	@Transactional
	public void save(OutboxEvent event) {

		String sql = """
				insert into outbox_event(
				    id,
				    event_id,
				    aggregate_id,
				    event_type,
				    topic,
				    payload,
				    status,
				    retry_count,
				    created_at,
				    published_at
				)
				values (
				    :id,
				    :eventId,
				    :aggregateId,
				    :eventType,
				    :topic,
				    :payload,
				    :status,
				    :retryCount,
				    :createdAt,
				    :publishedAt
				)
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", event.getId())
			.addValue("eventId", event.getEventId())
			.addValue("aggregateId", event.getAggregateId())
			.addValue("eventType", event.getEventType())
			.addValue("topic", event.getTopic())
			.addValue("payload", event.getPayload())
			.addValue("status", event.getStatus().name())
			.addValue("retryCount", event.getRetryCount())
			.addValue("createdAt", event.getCreatedAt())
			.addValue("publishedAt", event.getPublishedAt());

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
	public void markPublished(UUID id) {

		jdbc.update("""
				update outbox_event
				set status = 'PUBLISHED',
				    published_at = now()
				where id = :id
				""", Map.of("id", id));
	}

	@Transactional
	public void markFailed(UUID id) {

		jdbc.update("""
				update outbox_event
				set retry_count = retry_count + 1,
				    status = case
				        when retry_count + 1 >= 5 then 'FAILED'
				        else status
				    end
				where id = :id
				""", Map.of("id", id));
	}

	private RowMapper<OutboxEvent> rowMapper() {

		return (rs, rowNum) -> OutboxEvent.restore(UUID.fromString(rs.getString("id")),
				UUID.fromString(rs.getString("event_id")), rs.getString("aggregate_id"), rs.getString("event_type"),
				rs.getString("topic"), rs.getString("payload"), OutboxStatus.valueOf(rs.getString("status")),
				rs.getInt("retry_count"), rs.getTimestamp("created_at").toInstant(),
				toInstant(rs.getTimestamp("published_at")));
	}

	private static java.time.Instant toInstant(Timestamp timestamp) {
		return timestamp == null ? null : timestamp.toInstant();
	}

}