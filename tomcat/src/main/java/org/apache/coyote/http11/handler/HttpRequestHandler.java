package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.http11.request.HttpRequest;

public interface HttpRequestHandler {
    boolean support(HttpRequest httpRequest);

    void handle(HttpRequest httpRequest, OutputStream outputStream) throws IOException;
}
