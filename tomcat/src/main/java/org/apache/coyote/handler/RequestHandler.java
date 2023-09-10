package org.apache.coyote.handler;

public interface RequestHandler {

    Controller getHandler(final String requestUri);
}
