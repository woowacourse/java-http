package com.techcourse.exception;

import org.apache.coyote.http11.response.HttpStatusCode;

public class StaticResourceNotFoundException extends ApplicationException {

    public StaticResourceNotFoundException() {
        super(HttpStatusCode.NOT_FOUND, "Static Resource Not Found");
    }
}
