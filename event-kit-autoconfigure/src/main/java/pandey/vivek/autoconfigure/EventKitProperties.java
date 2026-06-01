package pandey.vivek.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "event-kit")
@Getter
@Setter
public class EventKitProperties {

	private String outboxTable = "outbox_event";

}