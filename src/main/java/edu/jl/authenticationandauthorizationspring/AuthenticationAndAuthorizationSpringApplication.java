package edu.jl.authenticationandauthorizationspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class AuthenticationAndAuthorizationSpringApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(AuthenticationAndAuthorizationSpringApplication.class, args);
        PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);

    }
}