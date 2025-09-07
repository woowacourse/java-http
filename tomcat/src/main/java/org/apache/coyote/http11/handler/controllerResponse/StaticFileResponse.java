package org.apache.coyote.http11.handler.controllerResponse;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.general.HttpHeader;
import org.apache.coyote.http11.httpResponse.HttpStatus;

public record StaticFileResponse(HttpStatus status, List<HttpHeader> headers, String content) implements ControllerResponse {

    public StaticFileResponse(HttpStatus status, String content) {
        this(status, new ArrayList<>(), content);
    }
}
