package account.repository;

import account.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventsRepository extends JpaRepository<Event, Long> {

    Event save(Event event);
}
