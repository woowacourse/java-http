package org.apache.coyote.handler;

public interface FrontController {

    Controller getHandler(final String requestUri);
}
