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

    private static final Map<String, Controller> controllers = new ConcurrentHashMap<>();

    static {
        controllers.put("/", new HomeController());
        controllers.put("/index", new IndexController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    private RequestMapping() {
    }

    public static Controller getController(HttpRequest request) {
        return controllers.keySet()
                .stream()
                .filter(key -> key.equals(request.getNativePath()))
                .findAny()
                .map(controllers::get)
                .orElseGet(ResourceController::getInstance);
    }

}
