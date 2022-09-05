package org.apache.coyote.http11;

public class FrontController {

    public HttpResponse performRequest(HttpRequest request) {
        HandlerMapping handler = HandlerMapping.findHandler(request);
        return handler.execute(request);
    }
}
