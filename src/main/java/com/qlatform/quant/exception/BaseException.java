package com.qlatform.quant.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {
    private final String message;
    private final HttpStatus status;

    protected BaseException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }
}
