package com.aaj.accountapi.adapters.rest.errorhandling;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
public class ErrorResponse {
    private final String message;
    private final List<String> details;
}
