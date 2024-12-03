package edu.jl.authenticationandauthorizationspring.exception;

public class NoRegisteredRoleException extends RuntimeException{
    public NoRegisteredRoleException(String message) {
        super(message);
    }
}
