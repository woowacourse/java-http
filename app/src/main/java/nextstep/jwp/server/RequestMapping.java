package nextstep.jwp.server;

import nextstep.jwp.server.http.request.HttpRequest;
import nextstep.jwp.web.application.LoginService;
import nextstep.jwp.web.application.RegisterService;
import nextstep.jwp.web.ui.Controller;
import nextstep.jwp.web.ui.DefaultController;
import nextstep.jwp.web.ui.LoginController;
import nextstep.jwp.web.ui.RegisterController;
import nextstep.jwp.web.ui.ResourceController;

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
