package org.apache.coyote.http11;

import java.io.IOException;

import org.apache.coyote.http11.httpmessage.request.HttpRequest;

@FunctionalInterface
public interface RequestHandler {
    String handle(HttpRequest httpRequest) throws IOException;
}
