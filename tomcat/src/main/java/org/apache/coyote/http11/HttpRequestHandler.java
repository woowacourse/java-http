package org.apache.coyote.http11;

public interface HttpRequestHandler {

    boolean support(HttpRequest httpRequest);

    void response(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;
}
