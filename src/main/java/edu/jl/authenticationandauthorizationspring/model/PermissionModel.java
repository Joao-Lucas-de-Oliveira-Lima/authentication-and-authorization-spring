package edu.jl.authenticationandauthorizationspring.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;


@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
public class PermissionModel implements GrantedAuthority{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long id;

    private String authority;

    public PermissionModel(String authority) {
        this.authority = authority;
    }
}
