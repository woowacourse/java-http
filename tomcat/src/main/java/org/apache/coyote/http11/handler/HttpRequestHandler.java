package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public interface HttpRequestHandler {
    boolean canHandle(HttpRequest request);

    HttpResponse handle(HttpRequest request) throws IOException;
}
