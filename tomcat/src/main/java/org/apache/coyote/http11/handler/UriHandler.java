package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public interface UriHandler {

    boolean canHandle(HttpRequest httpRequest);

    HandlerResponse getResponse(HttpRequest httpRequest) throws IOException;
}
