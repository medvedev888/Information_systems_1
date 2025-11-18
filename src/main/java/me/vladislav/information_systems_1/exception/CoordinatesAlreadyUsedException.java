package me.vladislav.information_systems_1.exception;

public class CoordinatesAlreadyUsedException extends RuntimeException {
    public CoordinatesAlreadyUsedException(String message) {
        super(message);
    }

    public CoordinatesAlreadyUsedException(String message, Throwable cause) {
        super(message, cause);
    }
}
