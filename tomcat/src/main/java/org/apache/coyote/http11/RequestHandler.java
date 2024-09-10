package org.apache.coyote.http11;

import java.io.IOException;

import org.apache.coyote.http11.httpmessage.request.Request;

@FunctionalInterface
public interface RequestHandler {
    String handle(Request request) throws IOException;
}