package edu.jl.authenticationandauthorizationspring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "Endpoints to generate a simple greeting.")
public class UserController {

    @GetMapping("/greetings")
    @Operation(summary = "Get Greeting", description = "Returns a greeting message from the admin", tags = "User")
    public ResponseEntity<Greeting> getGreeting() {
        return ResponseEntity.ok(new Greeting("Greetings from the admin! (Well, just one)."));
    }

    private record Greeting(String message) {
    }
}