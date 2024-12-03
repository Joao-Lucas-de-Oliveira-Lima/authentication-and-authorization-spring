package edu.jl.authenticationandauthorizationspring.exception;

import edu.jl.authenticationandauthorizationspring.dto.exception.ExceptionDto;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@RestController
@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handlerException(WebRequest webRequest, Exception exception){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildExceptionDto(webRequest, exception));
    }

    @ExceptionHandler(InvalidJwtAuthenticationException.class)
    public ResponseEntity<ExceptionDto> handlerJwtAuthenticationException(WebRequest webRequest, Exception exception){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(buildExceptionDto(webRequest, exception));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionDto> handlerUsernameNotFoundException(WebRequest webRequest, Exception exception){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildExceptionDto(webRequest, exception));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionDto> handlerBadCredentialsException(WebRequest webRequest, Exception exception){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildExceptionDto(webRequest, exception));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionDto> handlerBadRequestException(WebRequest webRequest, Exception exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildExceptionDto(webRequest, exception));
    }

    @ExceptionHandler(NoRegisteredRoleException.class)
    public ResponseEntity<ExceptionDto> handlerNoRegisteredException(WebRequest webRequest, Exception exception){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildExceptionDto(webRequest, exception));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionDto> handlerUserAlreadyExistsException(WebRequest webRequest, Exception exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildExceptionDto(webRequest, exception));
    }

    private ExceptionDto buildExceptionDto(WebRequest webRequest, Exception exception){
        return new ExceptionDto(new Date(), webRequest.getDescription(false), exception.getMessage());
    }
}
