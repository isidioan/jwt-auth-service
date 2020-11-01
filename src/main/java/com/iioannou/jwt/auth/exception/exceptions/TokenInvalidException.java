package com.iioannou.jwt.auth.exception.exceptions;

import java.util.function.Supplier;

/**
 * @author ioannou
 */
public class TokenInvalidException extends RuntimeException {

    public TokenInvalidException(Supplier<String> messageSupl) {
        super(messageSupl.get());
    }
}
