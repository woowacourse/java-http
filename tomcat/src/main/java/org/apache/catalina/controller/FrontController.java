package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FrontController {

    private final RequestMapping requestMapping;

    public FrontController(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();
        Controller controller = requestMapping.getController(path);
        controller.service(request, response);
    }
}
