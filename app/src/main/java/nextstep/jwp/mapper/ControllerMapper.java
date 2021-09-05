package nextstep.jwp.mapper;

import java.util.Map;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.controller.AbstractController;
import nextstep.jwp.http.request.HttpRequest;

public class ControllerMapper implements HandlerMapper {

    private final Map<String, AbstractController> controllers;

    public ControllerMapper(Map<String, AbstractController> controllers) {
        this.controllers = controllers;
    }

    @Override
    public Handler mapping(HttpRequest httpRequest) {
        return controllers.get(httpRequest.sourcePath().getValue());
    }
}
