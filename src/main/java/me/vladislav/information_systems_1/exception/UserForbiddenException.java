package me.vladislav.information_systems_1.exception;

public class UserForbiddenException extends RuntimeException {
    public UserForbiddenException(String message) {
        super(message);
    }

    public UserForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
