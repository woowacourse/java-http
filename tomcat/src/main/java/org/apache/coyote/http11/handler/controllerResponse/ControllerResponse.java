package org.apache.coyote.http11.handler.controllerResponse;

import org.apache.coyote.http11.general.HttpHeaders;
import org.apache.coyote.http11.httpResponse.HttpStatus;

public interface ControllerResponse {

    HttpStatus status();
    HttpHeaders headers();
    String content();

    void addHeader(String key, String value);
}
