package uz.ilmnajot.newsadsapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.ilmnajot.newsadsapp.entity.base.BaseEntity;
import uz.ilmnajot.newsadsapp.entity.base.BaseLongEntity;
import uz.ilmnajot.newsadsapp.entity.base.BaseUUIDEntity;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
//done
public class User extends BaseUUIDEntity implements UserDetails {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String fullName;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @Override
    // getAuthorities
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles
                .stream()
                .map(role->new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    // getPassword
    public String getPassword() {
        return this.password;
    }

    @Override
    // getUsername
    public String getUsername() {
        return this.username;
    }

    @Override
    // isAccountNonExpired
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    // isAccountNonLocked
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    // isCredentialsNonExpired
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    // isEnabled
    public boolean isEnabled() {
        return isActive != null && isActive;
    }
}
