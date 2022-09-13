package org.apache.coyote.http11.handler;

import java.util.HashMap;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponseHeader;

public interface RequestHandler {

    HandlerResponseEntity handle(final HttpRequest httpRequest);
}
