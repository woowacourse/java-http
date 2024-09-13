package com.techcourse.exception;

import org.apache.catalina.exception.TomcatException;
import org.apache.coyote.http11.response.HttpStatusCode;

public abstract class ApplicationException extends TomcatException {

    ApplicationException(HttpStatusCode httpStatusCode, String message) {
        super(httpStatusCode, message);
    }
}
