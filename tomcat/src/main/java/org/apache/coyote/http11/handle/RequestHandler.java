package org.apache.coyote.http11.handle;

import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;

public interface RequestHandler {
    void handle(HttpRequest request, HttpResponse response);
    boolean canHandle(HttpRequest request);
}
