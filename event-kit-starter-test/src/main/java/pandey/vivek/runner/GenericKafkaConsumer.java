package pandey.vivek.runner;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pandey.vivek.eventkit.registry.EventTypeRegistry;
import tools.jackson.databind.ObjectMapper;

@Component
public class GenericKafkaConsumer {

    private final ObjectMapper mapper;

    private final EventTypeRegistry registry;

    public GenericKafkaConsumer(ObjectMapper mapper, EventTypeRegistry registry) {
        this.mapper = mapper;
        this.registry = registry;
    }

    @KafkaListener(topics = "media-events")
    public void consume(ConsumerRecord<String, String> record) {
        String payload = record.value();
        String eventType = new String(record.headers().lastHeader("eventType").value());
        Class<?> clazz = registry.resolve(eventType);
        Object event = mapper.readValue(payload, clazz);
        System.out.println(event);
    }
}
