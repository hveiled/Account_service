package account.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    private final String username;
    private final String name;
    private final String lastname;
    private final String password;
    private final String email;
    private final Collection<SimpleGrantedAuthority> authorities;

    public UserDetailsImpl(User user) {
        this.username = user.getEmail().toLowerCase(Locale.ROOT);
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.password = user.getPassword();
        this.email = user.getEmail().toLowerCase(Locale.ROOT);
        Set<String> auth = user.getRoles();
        this.authorities = auth.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
