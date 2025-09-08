package org.apache.coyote.http11;

public interface HttpRequestHandler {

    boolean support(HttpRequest httpRequest);

    String response(HttpRequest httpRequest);
}
