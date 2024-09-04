package org.apache.coyote.http11;

import java.io.IOException;

public interface HttpRequestHandler {

    boolean supports(HttpRequest request);

    String handle(HttpRequest request) throws IOException;
}
