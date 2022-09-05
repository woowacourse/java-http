package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;

public interface Handler {

    HttpResponse handle(final HttpRequest httpRequest);
}
