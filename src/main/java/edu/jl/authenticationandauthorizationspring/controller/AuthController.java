package edu.jl.authenticationandauthorizationspring.controller;

import edu.jl.authenticationandauthorizationspring.dto.security.AccountCredentialsDto;
import edu.jl.authenticationandauthorizationspring.dto.security.CreateUserDto;
import edu.jl.authenticationandauthorizationspring.dto.security.TokenDto;
import edu.jl.authenticationandauthorizationspring.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate user and return access token", tags = "Authentication")
    public ResponseEntity<TokenDto> login(
            @RequestBody @Valid AccountCredentialsDto accountCredentialsDto,
            BindingResult bindingResult) throws BadRequestException {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            throw new BadRequestException("Invalid fields: " + errorMessage);
        }
        TokenDto token = authService.login(accountCredentialsDto);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    @Operation(summary = "User Registration", description = "Register a new user and return access token", tags = "Authentication")
    public ResponseEntity<TokenDto> register(
            @RequestBody @Valid CreateUserDto createUserDto,
            BindingResult bindingResult) throws BadRequestException {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            throw new BadRequestException("Invalid fields: " + errorMessage);
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(createUserDto));
    }
}