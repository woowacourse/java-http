package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HelloController;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.http.request.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> controllerMap = new HashMap<>();

    static {
        controllerMap.put("/", new HelloController());
        controllerMap.put("/login", new LoginController());
        controllerMap.put("/register", new RegisterController());
    }

    public static Controller getController(HttpRequest request) {
        return controllerMap.getOrDefault(request.getRequestURI(), new HomeController());
    }

    private RequestMapping() {
    }
}
