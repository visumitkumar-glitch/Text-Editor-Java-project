package com.editor.exception;

/** Thrown when an edit operation cannot be applied (bad position, bad range, empty text...). */
public class InvalidEditException extends RuntimeException {
    public InvalidEditException(String message) {
        super(message);
    }
}
