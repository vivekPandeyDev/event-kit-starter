package pandey.vivek.runner;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pandey.vivek.eventkit.processed.service.EventDeduplicator;
import tools.jackson.databind.ObjectMapper;

@Service
public class PublishListener {

    private final ObjectMapper objectMapper;

    private final EventDeduplicator dedup;

    public PublishListener(ObjectMapper objectMapper, EventDeduplicator dedup) {
        this.objectMapper = objectMapper;
        this.dedup = dedup;
    }

    @KafkaListener(topics = "media-events")
    @Transactional
    public void consume(String payload) {
        TestEvent event = objectMapper.readValue(payload, TestEvent.class);
        if (!dedup.firstProcessing(event.eventId(), "thumbnail-consumer")) {
            System.out.printf("DUPLICATE EVENT:");
            return;
        }
        System.out.printf("event: " + event);
    }
}
