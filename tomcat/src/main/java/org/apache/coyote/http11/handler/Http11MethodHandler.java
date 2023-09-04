package org.apache.coyote.http11.handler;

import org.apache.coyote.header.HttpMethod;

public interface Http11MethodHandler {

    HttpMethod supportMethod();

    String handle(final String request);
}
