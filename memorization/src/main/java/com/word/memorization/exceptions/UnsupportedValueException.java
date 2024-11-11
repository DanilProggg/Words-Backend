package com.word.memorization.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Using none correct value")
public class UnsupportedValueException extends RuntimeException {

    public UnsupportedValueException() {
        super("Using none correct value");
    }

    public UnsupportedValueException(String message) {
        super(message);
    }

    public UnsupportedValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedValueException(Throwable cause) {
        super(cause);
    }
}