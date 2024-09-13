package org.apache.catalina.exception;

import org.apache.coyote.http11.response.HttpStatusCode;

public class ControllerNotMatchedException extends ControllerException {

    public ControllerNotMatchedException() {
        super(HttpStatusCode.NOT_FOUND, "Controller not matched");
    }
}
