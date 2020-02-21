package com.aaj.accountapi.core.ports;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String exception) {
        super(exception);
    }
}
