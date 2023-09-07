package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;

public interface HttpHandler {

    boolean canHandle(HttpRequest httpRequest);

    ResponseEntity handle(HttpRequest httpRequest);
}
