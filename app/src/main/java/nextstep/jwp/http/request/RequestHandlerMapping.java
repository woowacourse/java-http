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

    private static final String EXTENSION_MARK = ".";

    private final Map<String, Controller> handlerMapping;

    public RequestHandlerMapping() {
        handlerMapping = new ConcurrentHashMap<>();
        doHandlerMapping();
    }

    private void doHandlerMapping() {
        handlerMapping.put("/", new HomeController());
        handlerMapping.put("/index", new IndexController());
        handlerMapping.put("/login", new LoginController());
        handlerMapping.put("/register", new RegisterController());
    }

    public Optional<Controller> getHandler(String path) {
        if (path.contains(EXTENSION_MARK)) {
            return Optional.of(new ResourceController());
        }
        return Optional.ofNullable(handlerMapping.get(path));
    }
}
