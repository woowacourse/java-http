package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;

public interface HttpRequestHandler {

    boolean supports(HttpRequest request);

    String handle(HttpRequest request) throws IOException;
}
