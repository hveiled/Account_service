package account.repository;

import account.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNameAndLastname(String name, String lastname);
    boolean existsByRoles(String role);
    void deleteByEmail(String email);
    List<User> findAll();
}
