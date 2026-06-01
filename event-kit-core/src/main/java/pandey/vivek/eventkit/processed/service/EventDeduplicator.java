package pandey.vivek.eventkit.processed.service;

import java.util.UUID;

public interface EventDeduplicator {

	boolean firstProcessing(UUID eventId, String consumer);

}