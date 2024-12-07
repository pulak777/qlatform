package com.qlatform.quant.exception.authentication;

import com.qlatform.quant.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends BaseException {
    public EmailAlreadyExistsException(String email, String message) {
        super(String.format("Failed for [%s]: %s", email, message), HttpStatus.BAD_REQUEST);
    }
}