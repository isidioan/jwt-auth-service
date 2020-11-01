package com.iioannou.jwt.auth.exception.exceptions;

import java.util.function.Supplier;

/**
 * @author ioannou
 */
public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(Supplier<String> message) {
        super(message.get());
    }
}
