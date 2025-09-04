package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface HttpRequestHandler {

    void handleGet(HttpRequest request, HttpResponse response);
}
