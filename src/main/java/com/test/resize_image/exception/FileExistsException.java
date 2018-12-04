package com.test.resize_image.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(org.springframework.http.HttpStatus.CONFLICT)
public class FileExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FileExistsException(String message) {
        super(message);
    }

}
