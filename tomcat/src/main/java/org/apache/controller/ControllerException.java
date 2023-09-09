package org.apache.controller;

import org.apache.coyote.http11.HttpMethod;

public class ControllerException extends RuntimeException{

    private static final String INVALID_HTTP_METHOD_REQUEST_MESSAGE = "잘못된 HTTP METHOD 요청입니다.";

    public ControllerException(HttpMethod httpMethod) {
        super(INVALID_HTTP_METHOD_REQUEST_MESSAGE + ": " + httpMethod.name());
    }
}
