package pandey.vivek.runner;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class GenericKafkaConsumer {

    private final ObjectMapper mapper;

    public GenericKafkaConsumer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @KafkaListener(topics = "media-events")
    public void consume(String payload) {
        Object event = mapper.readValue(payload, MediaEventTest.class);
        System.out.println(event);
    }
}
