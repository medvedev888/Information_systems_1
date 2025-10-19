package me.vladislav.information_systems_1.exception;

public class RelatedEntityDeletionException extends RuntimeException {
    public RelatedEntityDeletionException(String message) {
        super(message);
    }
}