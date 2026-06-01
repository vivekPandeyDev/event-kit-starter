package pandey.vivek.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pandey.vivek.eventkit.api.DomainEventPublisher;

import java.util.UUID;

@Component
public class PublishRunner implements CommandLineRunner {

    private final DomainEventPublisher publisher;

    public PublishRunner(DomainEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void run(String... args) {

        publisher.publish(new TestEvent(UUID.randomUUID(), UUID.randomUUID()));
        System.out.println("EVENT PUBLISHED");
    }
}