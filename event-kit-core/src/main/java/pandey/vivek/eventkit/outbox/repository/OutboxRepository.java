package pandey.vivek.eventkit.outbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pandey.vivek.eventkit.outbox.entity.OutboxEvent;

import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
}