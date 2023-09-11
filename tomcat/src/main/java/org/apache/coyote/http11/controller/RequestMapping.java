package org.apache.coyote.http11.controller;

import org.apache.coyote.Mapper;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapping implements Mapper {

    private static final Controller DEFAULT_CONTROLLER = new DefaultController();
    private final Map<String, Controller> controllerMapper = new HashMap<>();

    @Override
    public void addController(String path, Controller controller) {
        this.controllerMapper.put(path, controller);
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        Controller controller = controllerMapper.get(path);
        if (controller == null) {
            DEFAULT_CONTROLLER.service(request, response);
            return;
        }

        controller.service(request, response);
    }
}
