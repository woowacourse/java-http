package org.apache.catalina.dispatcher.handler;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public class ControllerHandler implements Handler{

    private final RequestMapping requestMapping;

    public ControllerHandler(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    @Override
    public boolean supports(HttpRequest request) {
        return requestMapping.findController(request.getPath()).isPresent();
    }

    @Override
    public String handle(HttpRequest request, HttpResponse response) throws IOException {
        Controller controller = requestMapping.findController(request.getPath()).orElseThrow();
        return controller.doService(request, response);
    }

}
