package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.NotFoundController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.http.request.HttpRequest;

public class RequestMapping {
    private static Map<String, Controller> HANDLER_MAP = new HashMap<>();

    static {
        HANDLER_MAP.put("/index.html", new IndexController());
        HANDLER_MAP.put("/register", new RegisterController());
        HANDLER_MAP.put("/login", new LoginController());
        HANDLER_MAP.put("NOT_FOUND", new NotFoundController());
    }

    public Controller getController(HttpRequest request) {
        return HANDLER_MAP.getOrDefault(request.getUri(), HANDLER_MAP.get("NOT_FOUND"));
    }
}
