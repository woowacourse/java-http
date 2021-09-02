package nextstep.jwp.ui;

import nextstep.jwp.application.LoginService;
import nextstep.jwp.application.RegisterService;
import nextstep.jwp.ui.controller.Controller;
import nextstep.jwp.ui.controller.DefaultController;
import nextstep.jwp.ui.controller.LoginController;
import nextstep.jwp.ui.controller.RegisterController;
import nextstep.jwp.ui.controller.ResourceController;
import nextstep.jwp.ui.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private static Map<String, Controller> CONTROLLERS = new HashMap<>();

    static {
        CONTROLLERS.put("/", new DefaultController());
        CONTROLLERS.put("/login", new LoginController(new LoginService()));
        CONTROLLERS.put("/register", new RegisterController(new RegisterService()));
    }

    public Controller getController(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        return CONTROLLERS.keySet().stream()
                .filter(mappingPath -> mappingPath.equals(path))
                .map(mappingPath -> CONTROLLERS.get(mappingPath))
                .findFirst()
                .orElse(new ResourceController());
    }
}
