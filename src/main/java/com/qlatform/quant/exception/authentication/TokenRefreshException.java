package com.qlatform.quant.exception.authentication;

import com.qlatform.quant.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TokenRefreshException extends BaseException {
    public TokenRefreshException(String token, String message) {
        super(String.format("Failed for [%s]: %s", token, message), HttpStatus.BAD_REQUEST);
    }
}