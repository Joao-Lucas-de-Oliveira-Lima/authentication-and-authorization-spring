package edu.jl.authenticationandauthorizationspring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/greetings")
    public ResponseEntity<Greeting> getGreeting() {
        return ResponseEntity.ok(new Greeting("Greetings from the admin! (Well, just one)."));
    }
    private record Greeting(String message) {
    }
}
