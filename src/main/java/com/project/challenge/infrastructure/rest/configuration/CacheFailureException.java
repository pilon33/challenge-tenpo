package com.project.challenge.infrastructure.rest.configuration;

public class CacheFailureException extends RuntimeException {

    public CacheFailureException(String message) {
        super(message);
    }

}
