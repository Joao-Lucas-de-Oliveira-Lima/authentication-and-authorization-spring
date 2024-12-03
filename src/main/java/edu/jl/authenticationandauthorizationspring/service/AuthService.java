package edu.jl.authenticationandauthorizationspring.service;

import edu.jl.authenticationandauthorizationspring.dto.security.AccountCredentialsDto;
import edu.jl.authenticationandauthorizationspring.dto.security.TokenDto;

public interface AuthService{
    public TokenDto login(AccountCredentialsDto accountCredentials);
}
