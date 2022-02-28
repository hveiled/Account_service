package account.service;

import account.model.*;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Component
public class UserAuthenticationService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    private static final Set<String> breachedPasswords = Set.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");

    public UserResponse signUp(User user) {
        boolean corruptedPassword = breachedPasswords.contains(user.getPassword());
        if (breachedPasswords.contains(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }
        if (userRepo.existsByEmail(user.getEmail().toLowerCase(Locale.ROOT))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }
        Set<String> role = new HashSet<>();
        if (!userRepo.existsByRoles("ROLE_ADMINISTRATOR")) {
            role.add("ROLE_ADMINISTRATOR");
        } else {
            role.add("ROLE_USER");
        }
        user.setRoles(role);
        user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);

        UserResponse.Builder responseBuilder = new UserResponse.Builder();
        UserResponse userResponse = responseBuilder
                .setId(user.getId())
                .setName(user.getName())
                .setLastname(user.getLastname())
                .setEmail(user.getEmail())
                .setRoles(user.getRoles())
                .build();
        return userResponse;
    }

    public User getPayment() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userRepo.findByEmail(userDetails.getUsername()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

    public UserResponse changeRole(UserRole userRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMINISTRATOR"));
        if (!hasAdminRole) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Administrator permissions required!");
        }
        if (!"GRANT".equals(userRole.getOperation()) && !"REMOVE".equals(userRole.getOperation())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid operation: " + userRole.getOperation());
        }
        if (!"USER".equals(userRole.getRole()) && !"ACCOUNTANT".equals(userRole.getRole())
                && !"ADMINISTRATOR".equals(userRole.getRole())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }
        User user = userRepo.findByEmail(userRole.getUser().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
        if (((user.getRoles().contains("ROLE_USER") || user.getRoles().contains("ROLE_ACCOUNTANT"))
                && userRole.getRole().equals("ADMINISTRATOR"))
                || (user.getRoles().contains("ROLE_ADMINISTRATOR")
                && (userRole.getRole().equals("ACCOUNTANT") || userRole.getRole().equals("USER")))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The user cannot combine administrative and business roles!");
        }
        Set<String> roles = user.getRoles();
        if ("GRANT".equals(userRole.getOperation())) {
            if (roles.contains("ROLE_" + userRole.getRole().toUpperCase())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Role already exists: " + userRole.getRole());
            }
            roles.add("ROLE_" + userRole.getRole());
        } else if ("REMOVE".equals(userRole.getOperation())) {
            if (!roles.contains("ROLE_" + userRole.getRole().toUpperCase())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
            }
            if (userRole.getRole().toUpperCase().equals("ADMINISTRATOR")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
            }
            if (user.getRoles().size() == 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
            }
            roles.remove("ROLE_" + userRole.getRole().toUpperCase());
        }
        user.setRoles(roles);
        userRepo.save(user);

        UserResponse.Builder builder = new UserResponse.Builder();

        UserResponse userResponse = builder
                .setId(user.getId())
                .setName(user.getName())
                .setLastname(user.getLastname())
                .setEmail(user.getEmail())
                .setRoles(user.getRoles())
                .build();
        return userResponse;
    }

    public Map<String, String> changeUserPassword(NewPassword password) {

        if (breachedPasswords.contains(password.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepo.findByEmail(userDetails.getUsername()).orElseThrow();
        if (passwordEncoder.matches(password.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        }
        user.setPassword(passwordEncoder.encode(password.getPassword()));
        User savedUser = userRepo.save(user);
        return Map.of("email", savedUser.getEmail(), "status", "The password has been updated successfully");
    }

    @Transactional
    public void deleteUser(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
        if (user.getRoles().contains("ROLE_ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }
        userRepo.deleteByEmail(email);
    }

    public List<UserResponse> getUserList() {
        return userRepo.findAll().stream()
            .map(user -> {
                UserResponse.Builder builder = new UserResponse.Builder();
                return builder
                        .setId(user.getId())
                        .setName(user.getName())
                        .setLastname(user.getLastname())
                        .setEmail(user.getEmail())
                        .setRoles(user.getRoles())
                        .build();
            }).collect(Collectors.toList());
    }
}
