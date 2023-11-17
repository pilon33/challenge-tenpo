package com.project.challenge.infrastructure.rest.configuration;

public class HttpRequestException extends RuntimeException {
    public HttpRequestException(String message) {
        super(message);
    }

}