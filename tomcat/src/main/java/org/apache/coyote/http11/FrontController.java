package org.apache.coyote.http11;

import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;

public class FrontController {

    public HttpResponse performRequest(HttpRequest request) {
        HandlerMapping handler = HandlerMapping.findHandler(request);
        return handler.execute(request);
    }
}
