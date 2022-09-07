package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.message.HttpRequest;

public interface RequestHandler {

    ResponseEntity handle(HttpRequest httpRequest);
}
