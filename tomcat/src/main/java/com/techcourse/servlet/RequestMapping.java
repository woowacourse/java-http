package com.techcourse.servlet;

import com.techcourse.controller.Controller;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> pathControllerMapper;

    public RequestMapping(Map<String, Controller> pathControllerMapper) {
        this.pathControllerMapper = pathControllerMapper;
    }

    public Optional<Controller> getController(HttpRequest request) {
        String path = request.getPath();
        return Optional.ofNullable(pathControllerMapper.get(path));
    }
}
