package org.apache.coyote.http11;

import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;

public class FrontController {

    public void performRequest(HttpRequest request, HttpResponse response) {
        HandlerMapping handler = HandlerMapping.findHandler(request);
        handler.execute(request, response);
    }
}
