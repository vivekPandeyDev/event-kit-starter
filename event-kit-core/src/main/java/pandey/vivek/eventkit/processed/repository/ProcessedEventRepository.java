package pandey.vivek.eventkit.processed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pandey.vivek.eventkit.processed.entity.ProcessedEvent;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {

}