package org.apache.coyote.handler.mapping;

import org.apache.coyote.http.request.HttpRequest;

import java.io.IOException;

public interface HandlerMapping {

    boolean supports(final HttpRequest httpRequest);

    String handle(final HttpRequest httpRequest) throws IOException;
}
