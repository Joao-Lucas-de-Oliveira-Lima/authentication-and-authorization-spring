package edu.jl.authenticationandauthorizationspring.service.implementation;

import edu.jl.authenticationandauthorizationspring.dto.security.AccountCredentialsDto;
import edu.jl.authenticationandauthorizationspring.dto.security.TokenDto;
import edu.jl.authenticationandauthorizationspring.service.AuthService;
import edu.jl.authenticationandauthorizationspring.service.UserService;
import edu.jl.authenticationandauthorizationspring.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImplementation implements AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthServiceImplementation(
            UserService userService,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
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
}
