package edu.jl.authenticationandauthorizationspring.service.implementation;

import edu.jl.authenticationandauthorizationspring.model.PermissionModel;
import edu.jl.authenticationandauthorizationspring.model.UserModel;
import edu.jl.authenticationandauthorizationspring.repository.UserRepository;
import edu.jl.authenticationandauthorizationspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImplementation implements UserDetailsService, UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username "+username+" not found!"));
    }

    public Boolean existsByUsername(String username){
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserModel save(String username, String password, List<PermissionModel> roles) {
        UserModel user = new UserModel(username, password, roles);
        return userRepository.save(user);
    }
}
