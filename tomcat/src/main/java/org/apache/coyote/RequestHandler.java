package org.apache.coyote;

public interface RequestHandler {

    void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;
}
