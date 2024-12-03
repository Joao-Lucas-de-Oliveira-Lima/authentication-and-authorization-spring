package edu.jl.authenticationandauthorizationspring.dto.exception;

import java.util.Date;

public record ExceptionDto(
        Date timestamp,
        String details,
        String message) {

}