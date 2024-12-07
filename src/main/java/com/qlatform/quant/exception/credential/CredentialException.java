package com.qlatform.quant.exception.credential;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CredentialException {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class CredentialStorageException extends RuntimeException {
        public CredentialStorageException(String message) {
            super(message);
        }

        public CredentialStorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class CredentialNotFoundException extends RuntimeException {
        public CredentialNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class CredentialRetrievalException extends RuntimeException {
        public CredentialRetrievalException(String message) {
            super(message);
        }

        public CredentialRetrievalException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class CredentialDeletionException extends RuntimeException {
        public CredentialDeletionException(String message) {
            super(message);
        }

        public CredentialDeletionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class CredentialEncryptionException extends RuntimeException {
        public CredentialEncryptionException(String message) {
            super(message);
        }

        public CredentialEncryptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidCredentialException extends RuntimeException {
        public InvalidCredentialException(String message) {
            super(message);
        }
    }
}