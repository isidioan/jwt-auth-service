package com.iioannou.jwt.auth.exception.handler;

import com.iioannou.jwt.auth.exception.exceptions.TokenExpiredException;
import com.iioannou.jwt.auth.exception.exceptions.TokenInvalidException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author ioannou
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TokenExpiredException.class)
    public final ResponseEntity<Boolean> handleException(TokenExpiredException expiredException) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).header(expiredException.getClass().getCanonicalName(), "true").body(false);
    }

    @ExceptionHandler(TokenInvalidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final String handleException(TokenInvalidException ex) {
        return ex.getMessage();
    }
}
