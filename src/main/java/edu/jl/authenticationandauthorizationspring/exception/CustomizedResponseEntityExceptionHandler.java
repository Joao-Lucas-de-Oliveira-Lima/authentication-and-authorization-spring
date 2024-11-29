package edu.jl.authenticationandauthorizationspring.exception;

import edu.jl.authenticationandauthorizationspring.dto.exception.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@RestController
@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidJwtAuthenticationException.class)
    public ResponseEntity<ExceptionDto> handlerJwtAuthenticationException(WebRequest webRequest, Exception exception){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(buildExceptionDto(webRequest, exception));
    }

    private ExceptionDto buildExceptionDto(WebRequest webRequest, Exception exception){
        return new ExceptionDto(new Date(), webRequest.getDescription(false), exception.getMessage());
    }
}
