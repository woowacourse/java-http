package org.apache.coyote.http11.request.mapping;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

@FunctionalInterface
public interface RequestHandler {

    HttpResponse handle(final HttpRequest httpRequest);
}
