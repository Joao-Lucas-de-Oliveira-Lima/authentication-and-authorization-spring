package edu.jl.authenticationandauthorizationspring.controller;

import edu.jl.authenticationandauthorizationspring.dto.security.AccountCredentialsDto;
import edu.jl.authenticationandauthorizationspring.dto.security.TokenDto;
import edu.jl.authenticationandauthorizationspring.service.implementation.AuthServiceImplementation;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceImplementation authServiceImplementation;

    @Autowired
    public AuthController(AuthServiceImplementation authServiceImplementation) {
        this.authServiceImplementation = authServiceImplementation;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(
            @RequestBody @Valid AccountCredentialsDto accountCredentialsDto,
            BindingResult bindingResult) throws BadRequestException {
        if(bindingResult.hasErrors()){
            throw new BadRequestException("Campos incorretos!");
        }
        return ResponseEntity.ok(authServiceImplementation.login(accountCredentialsDto));
    }
}
