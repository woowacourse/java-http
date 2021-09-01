package nextstep.jwp.http.request;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.service.Service;

public class RequestHandlerMapping {

    private static final Controller HOME_CONTROLLER = new HomeController();
    private static final Controller INDEX_CONTROLLER = new IndexController();
    private static final Service LOGIN_SERVICE = new LoginService();
    private static final Controller LOGIN_CONTROLLER = new LoginController(LOGIN_SERVICE);
    private static final Service REGISTER_SERVICE = new RegisterService();
    private static final Controller REGISTER_CONTROLLER = new RegisterController(REGISTER_SERVICE);
    private static final String EXTENSION_MARK = ".";
    private static final Controller RESOURCE_CONTROLLER = new ResourceController();

    private final Map<String, Controller> handlerMapping;

    public RequestHandlerMapping() {
        handlerMapping = new ConcurrentHashMap<>();
        doHandlerMapping();
    }

    private void doHandlerMapping() {
        handlerMapping.put("/", HOME_CONTROLLER);
        handlerMapping.put("/index", INDEX_CONTROLLER);
        handlerMapping.put("/login", LOGIN_CONTROLLER);
        handlerMapping.put("/register", REGISTER_CONTROLLER);
    }

    public Optional<Controller> getHandler(String path) {
        if (path.contains(EXTENSION_MARK)) {
            return Optional.of(RESOURCE_CONTROLLER);
        }
        return Optional.ofNullable(handlerMapping.get(path));
    }
}
