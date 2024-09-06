package org.apache.coyote.http11;

public interface RequestHandler {

    boolean canHandle(HttpRequest request);

    HttpResponse handle(HttpRequest request);
}
