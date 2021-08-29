package nextstep.jwp.http.request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;

public class RequestHandlerMapping {

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

    public Controller getHandler(String path) {
        return handlerMapping.get(path);
    }
}
