package org.apache.coyote;

import java.io.IOException;

public interface RequestHandler {

    boolean canHandling(HttpRequest httpRequest) throws IOException;

    String getContentType(HttpRequest httpRequest);

    String getResponseBody(HttpRequest httpRequest) throws IOException;
}
