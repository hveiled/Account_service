package account.service;

import account.model.Event;
import account.repository.EventsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventsService {

    @Autowired
    EventsRepository eventsRepo;

    public List<Event> getEvents() {
        return eventsRepo.findAll();
    }

    public Event addNewEvent(Event event) {
        return eventsRepo.save(event);
    }
}
