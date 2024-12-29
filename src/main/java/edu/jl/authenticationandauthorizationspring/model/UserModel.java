package edu.jl.authenticationandauthorizationspring.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
public class UserModel implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_permission",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id")}
    )
    private List<PermissionModel> permissions;
    @Column(name = "is_account_non_expired")
    private boolean isAccountNonExpired;
    @Column(name = "is_account_non_locked")
    private boolean isAccountNonLocked;
    @Column(name = "is_credentials_non_expired")
    private boolean isCredentialsNonExpired;
    @Column(name = "is_enabled")
    private boolean isEnabled;

    public UserModel(String username, String password, List<PermissionModel> permissions) {
        this.username = username;
        this.password = password;
        this.permissions = permissions;
    }

    public List<String> getRoles() {
        return permissions.stream().map(PermissionModel::getAuthority).collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions;
    }
}
