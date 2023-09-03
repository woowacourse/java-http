package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;

public interface HttpRequestHandler {
    boolean support(HttpRequest httpRequest);

    void handle(HttpRequest httpRequest, OutputStream outputStream) throws IOException;
}
