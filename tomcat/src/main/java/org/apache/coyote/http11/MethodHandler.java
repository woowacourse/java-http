package org.apache.coyote.http11;

public interface MethodHandler {
    void handle(HttpRequest request, HttpResponseNew response);
}
