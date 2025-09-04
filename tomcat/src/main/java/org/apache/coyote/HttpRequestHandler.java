package org.apache.coyote;

public interface HttpRequestHandler {

    void handleGet(HttpRequest request, HttpResponse response);
}
