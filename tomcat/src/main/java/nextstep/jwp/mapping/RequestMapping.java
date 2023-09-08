package nextstep.jwp.mapping;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.http.request.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> CONTROLLERS = new ConcurrentHashMap<>();

    static {
        CONTROLLERS.put("/", new HomeController());
        CONTROLLERS.put("/index", new IndexController());
        CONTROLLERS.put("/login", new LoginController());
        CONTROLLERS.put("/register", new RegisterController());
    }

    private RequestMapping() {
    }

    public static Controller getController(HttpRequest request) {
        return CONTROLLERS.keySet()
                .stream()
                .filter(key -> key.equals(request.getNativePath()))
                .findAny()
                .map(CONTROLLERS::get)
                .orElseGet(ResourceController::getInstance);
    }

}
