package org.apache.coyote.handler;

import org.apache.coyote.dto.HttpRequest;

public interface RequestHandler {
    String handle(HttpRequest httpRequest);
}
