package org.apache.coyote.http11.handler.controllerResponse;

import org.apache.coyote.http11.httpResponse.HttpStatus;

public interface ControllerResponse {

    HttpStatus status();
    String content();
}
