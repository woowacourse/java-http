package org.apache.catalina.core.controller;

import jakarta.http.reqeust.HttpRequest;
import jakarta.http.response.HttpResponse;
import org.apache.catalina.core.config.Configuration;

public class ControllerContainer {

    private final RequestMapping requestMapping = new RequestMapping();

    public ControllerContainer(final Configuration configuration) {
        configuration.addController(requestMapping);
        configuration.setExceptionHandler(requestMapping);
        configuration.setResourceController(requestMapping);
    }

    public void service(final HttpRequest request, final HttpResponse response) {
        requestMapping.service(request, response);
    }
}
