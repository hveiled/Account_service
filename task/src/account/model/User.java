package account.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;

@ToString
@RequiredArgsConstructor
@Entity
//@Data
@Proxy(lazy = false)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    @JsonProperty("id")
    private Long id;

    @NotNull
    @NotEmpty
    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    @NotNull
    @NotEmpty
    @Column(name = "last_name")
    @JsonProperty("lastname")
    private String lastname;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "\\w+@acme.com", message = "Invalid email")
    @Column(name = "email")
    @JsonProperty("email")
    private String email;

    @NotNull
    @NotEmpty
    @JsonProperty("password")
    @JsonIgnore
    @Column(name = "password")
    @Size(min = 12)
    private String password;

    @Column(name = "roles")
    @ElementCollection(fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<String> roles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> role) {
        this.roles = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
