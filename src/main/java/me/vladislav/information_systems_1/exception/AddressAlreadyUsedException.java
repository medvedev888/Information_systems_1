package me.vladislav.information_systems_1.exception;

public class AddressAlreadyUsedException extends RuntimeException {
    public AddressAlreadyUsedException(String message) {
        super(message);
    }

    public AddressAlreadyUsedException(String message, Throwable cause) {
        super(message, cause);
    }
}
