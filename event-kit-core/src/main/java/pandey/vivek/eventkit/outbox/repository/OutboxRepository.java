package pandey.vivek.eventkit.outbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pandey.vivek.eventkit.outbox.entity.OutboxEvent;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

	@Query(value = """
			select *
			from outbox_event
			where status='PENDING'
			order by created_at
			for update skip locked
			limit :limit
			""", nativeQuery = true)
	List<OutboxEvent> lockPendingEvents(@Param("limit") int limit);

}