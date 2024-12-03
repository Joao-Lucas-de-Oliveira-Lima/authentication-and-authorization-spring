package edu.jl.authenticationandauthorizationspring.service;

import edu.jl.authenticationandauthorizationspring.model.PermissionModel;
import edu.jl.authenticationandauthorizationspring.model.UserModel;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    Boolean existsByUsername(String username);
    UserModel save(String username, String password, List<PermissionModel> roles);
}
