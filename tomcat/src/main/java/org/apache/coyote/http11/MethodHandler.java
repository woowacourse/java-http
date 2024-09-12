package org.apache.coyote.http11;

public interface MethodHandler {
    HttpResponse handle(HttpRequest request);
}
