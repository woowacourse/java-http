package org.apache.coyote.http11;

import java.io.IOException;

import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;

@FunctionalInterface
public interface RequestHandler {
    void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
