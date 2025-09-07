package org.apache.coyote.http11.handler.controllerResponse;

import java.util.List;
import org.apache.coyote.http11.general.HttpHeader;
import org.apache.coyote.http11.httpResponse.HttpStatus;

public interface ControllerResponse {

    HttpStatus status();
    List<HttpHeader> headers();
    String content();
}
