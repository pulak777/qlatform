package com.qlatform.quant.exception.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class JwtGenerationException extends JwtException {
    public JwtGenerationException(String message) {
        super(message);
    }

    public JwtGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}