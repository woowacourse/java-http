package org.apache.coyote.handler;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface RequestHandler {


    HttpResponse handle(final HttpRequest httpRequest);
}
