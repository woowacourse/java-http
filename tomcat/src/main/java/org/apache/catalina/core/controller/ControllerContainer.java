package org.apache.catalina.core.controller;

import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
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
