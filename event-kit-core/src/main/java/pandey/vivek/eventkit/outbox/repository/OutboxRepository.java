package pandey.vivek.eventkit.outbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pandey.vivek.eventkit.outbox.entity.OutboxEvent;
import pandey.vivek.eventkit.outbox.enums.OutboxStatus;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

	List<OutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);

}