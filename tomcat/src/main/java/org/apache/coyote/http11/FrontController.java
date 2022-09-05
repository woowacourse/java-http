package org.apache.coyote.http11;

import java.util.function.Function;

public class FrontController {

    private final HandlerMapping handlerMapping;

    public FrontController() {
        this.handlerMapping = new HandlerMapping();
    }

    public HttpResponse performRequest(HttpRequest request) {
        Function<HttpRequest, HttpResponse> controller = handlerMapping.findController(request);
        return controller.apply(request);
    }
}
