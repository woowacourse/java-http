package org.apache.coyote.requestmapping.handler;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public class NotFoundRequestHandler implements Handler {
    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        return HttpResponse.notFound().build();
    }
}
