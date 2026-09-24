package com.techcourse.controller;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ControllerMapping;
import org.apache.coyote.Adapter;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.util.Objects;

public final class ApplicationAdapter implements Adapter {

    private final ControllerMapping controllerMapping;

    public ApplicationAdapter(ControllerMapping controllerMapping) {
        this.controllerMapping = Objects.requireNonNull(controllerMapping);
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        Controller controller = controllerMapping.getController(request);
        controller.service(request, response);
    }
}
