package org.apache.coyote.http11.handle.handler;

import org.apache.coyote.http11.reqeust.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface HttpHandler {

    HttpResponse handle(final HttpRequest request);

    boolean canHandle(final HttpRequest request);
}
