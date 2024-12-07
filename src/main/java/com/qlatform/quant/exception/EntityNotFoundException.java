package com.qlatform.quant.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BaseException {
    public EntityNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public EntityNotFoundException(String entityName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", entityName, fieldName, fieldValue),
                HttpStatus.NOT_FOUND);
    }
}


