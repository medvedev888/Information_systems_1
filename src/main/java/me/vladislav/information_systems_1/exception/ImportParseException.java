package me.vladislav.information_systems_1.exception;

public class ImportParseException extends RuntimeException {
    public ImportParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImportParseException(String message) {
        super(message);
    }
}
