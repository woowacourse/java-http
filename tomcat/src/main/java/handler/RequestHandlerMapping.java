package handler;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;

public class RequestHandlerMapping implements RequestHandler {

    private final Map<String, Controller> handlerMapping = new HashMap<>();

    public RequestHandlerMapping() {
        handlerMapping.put("/", new IndexController());
        handlerMapping.put("/login", new LoginController());
        handlerMapping.put("/register", new RegisterController());
    }

    @Override
    public Controller getHandler(final String requestUri) {
        return handlerMapping.get(requestUri);
    }
}
