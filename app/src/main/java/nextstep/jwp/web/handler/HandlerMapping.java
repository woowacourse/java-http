package nextstep.jwp.web.handler;

import nextstep.jwp.web.ControllerMapping;
import nextstep.jwp.web.http.request.HttpRequest;

public class HandlerMapping {

    private HandlerMapping() {
    }

    public static HttpRequestHandler getHandler(HttpRequest request) {
        if (request.getUrl().contains(".")) {
            return new StaticFileRequestHandlerImpl();
        }

        return ControllerMapping.getController(request.getUrl());
    }
}
