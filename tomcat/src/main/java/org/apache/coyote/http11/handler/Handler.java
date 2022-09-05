package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public interface Handler {
    HttpResponse handle(final HttpRequest httpRequest);
}
