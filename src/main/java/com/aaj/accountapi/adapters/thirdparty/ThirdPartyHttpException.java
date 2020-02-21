package com.aaj.accountapi.adapters.thirdparty;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ThirdPartyHttpException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

    public ThirdPartyHttpException(HttpStatus httpStatus, String message){
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
