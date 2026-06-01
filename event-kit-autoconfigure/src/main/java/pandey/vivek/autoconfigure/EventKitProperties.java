package pandey.vivek.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "event-kit")
@Getter
@Setter
public class EventKitProperties {

	private Outbox outbox = new Outbox();

	@Getter
	@Setter
	public static class Outbox {

		private int batchSize = 50;

		private long publishDelayMs = 5000;

	}

}