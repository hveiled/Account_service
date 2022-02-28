package account.controller;

import account.model.Event;
import account.model.UserAccess;
import account.service.EventsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventsController {

    private final EventsService service;

    @GetMapping("/api/security/events")
    public List<Event> events(@RequestBody @Valid UserAccess userAccess) {
        return service.getEvents();
    }
}
