package account.controller;

import account.model.Event;
import account.model.NewPassword;
import account.model.User;
import account.model.UserResponse;
import account.service.EventsService;
import account.service.UserAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
//import org.slf4j.Logger;
//import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthenticationController {

    private final UserAuthenticationService userAuthService;
    private final EventsService eventsService;
    private static final Logger logger = LogManager.getLogger(Event.class);
    private static final Marker security = MarkerManager.getMarker("SECURITY");

    @Autowired
    public AuthenticationController(UserAuthenticationService userAuthService, EventsService eventsService) {
        this.userAuthService = userAuthService;
        this.eventsService = eventsService;
    }

    @PostMapping("/signup")
    public UserResponse signup(@Valid @NotNull @RequestBody User user) {
        logger.info(security, "login attempt by user: {}", user.getEmail());
        eventsService.addNewEvent(new Event());
        return userAuthService.signUp(user);
    }

    @PostMapping("/changepass")
    public Map<String, String> changePass(@Valid @RequestBody NewPassword newPassword) {
        return userAuthService.changeUserPassword(newPassword);
    }
}
