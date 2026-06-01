package pandey.vivek.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pandey.vivek.eventkit.api.DomainEventPublisher;

@Component
public class BeanTestRunner implements CommandLineRunner {

    private final DomainEventPublisher publisher;

    public BeanTestRunner(DomainEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void run(String... args) {
        System.out.println(publisher.getClass());
    }
}