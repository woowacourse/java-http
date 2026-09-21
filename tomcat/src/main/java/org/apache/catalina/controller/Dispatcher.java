package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.coyote.Adapter;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public class Dispatcher implements Adapter {

    private final RequestMapping requestMapping;
    private final Controller staticResourceController;

    public Dispatcher(RequestMapping requestMapping, Controller staticResourceController) {
        this.requestMapping = requestMapping;
        this.staticResourceController = staticResourceController;
    }

    @Override
    public HttpResponse service(HttpRequest request) throws IOException {
        return requestMapping.getController(request)
                .orElse(staticResourceController)
                .service(request);
    }
}
