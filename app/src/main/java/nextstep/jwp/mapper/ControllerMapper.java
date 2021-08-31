package nextstep.jwp.mapper;

import nextstep.jwp.handler.controller.AbstractController;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.controller.LoginController;
import nextstep.jwp.handler.controller.RegisterController;
import nextstep.jwp.http.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapper implements HandlerMapper {

    private final Map<String, AbstractController> controllers = new HashMap<>();

    public ControllerMapper() {
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    @Override
    public Handler mapping(HttpRequest httpRequest) {
        return controllers.get(httpRequest.sourcePath().getValue());
    }
}
