package pandey.vivek.eventkit.util;

import java.sql.Timestamp;
import java.time.Instant;

public final class JdbcConverters {

	private JdbcConverters() {
	}

	public static Timestamp toTimestamp(Instant instant) {
		return instant == null ? null : Timestamp.from(instant);
	}

	public static Instant toInstant(Timestamp timestamp) {
		return timestamp == null ? null : timestamp.toInstant();
	}

}