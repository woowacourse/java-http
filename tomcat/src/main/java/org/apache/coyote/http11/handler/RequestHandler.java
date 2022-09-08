package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.handler.ResponseEntity;
import org.apache.coyote.http11.http.HttpRequest;

public interface RequestHandler {

    ResponseEntity handle(HttpRequest httpRequest);
}
