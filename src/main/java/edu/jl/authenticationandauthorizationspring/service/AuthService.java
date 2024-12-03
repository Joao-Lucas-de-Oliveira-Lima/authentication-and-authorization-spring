package edu.jl.authenticationandauthorizationspring.service;

import edu.jl.authenticationandauthorizationspring.dto.security.AccountCredentialsDto;
import edu.jl.authenticationandauthorizationspring.dto.security.CreateUserDto;
import edu.jl.authenticationandauthorizationspring.dto.security.TokenDto;

public interface AuthService{
    TokenDto login(AccountCredentialsDto accountCredentials);
    TokenDto register(CreateUserDto createUserDto);
}
