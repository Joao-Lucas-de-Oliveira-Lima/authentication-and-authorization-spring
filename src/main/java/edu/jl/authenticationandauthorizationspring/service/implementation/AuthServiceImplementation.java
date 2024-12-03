package edu.jl.authenticationandauthorizationspring.service.implementation;

import edu.jl.authenticationandauthorizationspring.dto.security.AccountCredentialsDto;
import edu.jl.authenticationandauthorizationspring.dto.security.CreateUserDto;
import edu.jl.authenticationandauthorizationspring.dto.security.TokenDto;
import edu.jl.authenticationandauthorizationspring.exception.NoRegisteredRoleException;
import edu.jl.authenticationandauthorizationspring.exception.UserAlreadyExistsException;
import edu.jl.authenticationandauthorizationspring.model.PermissionModel;
import edu.jl.authenticationandauthorizationspring.model.UserModel;
import edu.jl.authenticationandauthorizationspring.service.AuthService;
import edu.jl.authenticationandauthorizationspring.service.PermissionService;
import edu.jl.authenticationandauthorizationspring.service.UserService;
import edu.jl.authenticationandauthorizationspring.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthServiceImplementation implements AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final PermissionService permissionService;

    @Autowired
    public AuthServiceImplementation(
            UserService userService,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            PermissionService permissionService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.permissionService = permissionService;
    }

    public TokenDto login(AccountCredentialsDto accountCredentials) {
        String username = accountCredentials.getUsername();
        String password = accountCredentials.getPassword();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            UserDetails userDetails = userService.loadUserByUsername(username);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return jwtTokenProvider.getAccessToken(username, roles);

        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("Invalid username: " + username);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect password for username: " + username);
        }
    }

    public TokenDto register(CreateUserDto createUserDto) throws UserAlreadyExistsException, NoRegisteredRoleException {
        String username = createUserDto.getUsername();
        String password = createUserDto.getPassword();
        List<String> roles = createUserDto.getRoles();

        if (userService.existsByUsername(username)) {
            throw new UserAlreadyExistsException("The username is already in use!");
        }

        Map<String, PermissionModel> registeredPermissions = permissionService.getPermissionsMap();
        if (registeredPermissions.isEmpty()) {
            throw new NoRegisteredRoleException("No ROLE found in the system!");
        }

        List<PermissionModel> userPermissions = roles.stream()
                .filter(registeredPermissions::containsKey)
                .map(PermissionModel::new)
                .toList();

        // If no valid roles are provided, assign the default role "ROLE_GUEST"
        if (userPermissions.isEmpty()) {
            userPermissions = List.of(new PermissionModel("ROLE_GUEST"));
        }

        String encryptedPassword = passwordEncoder.encode(password);
        UserModel savedUser = userService.save(username, encryptedPassword, userPermissions);

        List<String> authorities = userPermissions.stream()
                .map(PermissionModel::getAuthority)
                .toList();

        return jwtTokenProvider.getAccessToken(savedUser.getUsername(), authorities);
    }

}
