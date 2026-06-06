package pandey.vivek.eventkit.processed.service;

import java.util.UUID;

public interface EventDeduplicate {

	boolean firstProcessing(UUID eventId, String consumer);

}