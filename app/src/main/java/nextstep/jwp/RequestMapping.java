package nextstep.jwp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.controller.BaseController;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticFileController;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.RegisterService;

public class RequestMapping {

    private final Map<String, Controller> controllers = new ConcurrentHashMap<>();

    public RequestMapping() {
        controllers.put("/", new BaseController());
        controllers.put("/login", new LoginController(new LoginService()));
        controllers.put("/register", new RegisterController(new RegisterService()));
    }

    public Controller getController(String uri) {
        return controllers.getOrDefault(uri, new StaticFileController());
    }
}
