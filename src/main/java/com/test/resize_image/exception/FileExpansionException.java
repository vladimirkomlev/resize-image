package com.test.resize_image.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(org.springframework.http.HttpStatus.LENGTH_REQUIRED)
public class FileExpansionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FileExpansionException(String message) {
        super(message);
    }

    public FileExpansionException(String message, Throwable cause) {
        super(message, cause);
    }

}
