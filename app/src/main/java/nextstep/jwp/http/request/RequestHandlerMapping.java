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

public class RequestHandlerMapping {

    private static final Controller HOME = new HomeController();
    private static final Controller INDEX = new IndexController();
    private static final Controller LOGIN = new LoginController();
    private static final Controller REGISTER = new RegisterController();
    private static final String EXTENSION_MARK = ".";
    private static final Controller RESOURCE = new ResourceController();

    private final Map<String, Controller> handlerMapping;

    public RequestHandlerMapping() {
        handlerMapping = new ConcurrentHashMap<>();
        doHandlerMapping();
    }

    private void doHandlerMapping() {
        handlerMapping.put("/", HOME);
        handlerMapping.put("/index", INDEX);
        handlerMapping.put("/login", LOGIN);
        handlerMapping.put("/register", REGISTER);
    }

    public Optional<Controller> getHandler(String path) {
        if (path.contains(EXTENSION_MARK)) {
            return Optional.of(RESOURCE);
        }
        return Optional.ofNullable(handlerMapping.get(path));
    }
}
