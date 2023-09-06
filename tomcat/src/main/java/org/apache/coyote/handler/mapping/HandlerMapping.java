package org.apache.coyote.handler.mapping;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

import java.io.IOException;

public interface HandlerMapping {

    boolean supports(final HttpRequest httpRequest);

    String handle(final HttpRequest httpRequest) throws IOException;

    HttpResponse handle2(final HttpRequest httpRequest) throws IOException;
}
