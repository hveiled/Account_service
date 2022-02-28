package account.controller;

import account.model.UserResponse;
import account.model.UserRole;
import account.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class UserFunctionalityController {

    private final UserAuthenticationService service;

    @Autowired
    public UserFunctionalityController(UserAuthenticationService service) {
        this.service = service;
    }

    @PutMapping("/user")
    public Map<String, String> user(String email) {
        return Map.of("status", "User " + email + " <operation>");
    }

    @PutMapping("/user/role")
    public UserResponse changeRole(@RequestBody @Valid UserRole userRole) {
        return service.changeRole(userRole);
    }

    @DeleteMapping("/user/{email}")
    public Map<String, String> delete(@PathVariable @Valid String email) {
        service.deleteUser(email);
        return Map.of("user", email, "status", "Deleted successfully!");
    }

    @GetMapping("/user")
    public List<UserResponse> getUsers() {
        return service.getUserList();
    }
}
