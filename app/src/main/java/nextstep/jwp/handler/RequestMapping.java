package nextstep.jwp.handler;

import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ErrorController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.handler.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Controller> controllerMap = new HashMap<>();

    public RequestMapping() {
        controllerMap.put("/login", new LoginController());
        controllerMap.put("/register", new RegisterController());
    }

    public Controller findController(HttpRequest httpRequest) {
        return controllerMap.getOrDefault(httpRequest.getRequestUrl(), new ErrorController());
    }
}
