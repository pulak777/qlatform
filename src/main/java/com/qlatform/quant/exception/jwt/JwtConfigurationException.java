package com.qlatform.quant.exception.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class JwtConfigurationException extends JwtException {
    public JwtConfigurationException(String message) {
        super(message);
    }

    public JwtConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
