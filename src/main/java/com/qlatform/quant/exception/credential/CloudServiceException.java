package com.qlatform.quant.exception.credential;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CloudServiceException extends RuntimeException {
    public CloudServiceException(String message) {
        super(message);
    }

    public CloudServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}