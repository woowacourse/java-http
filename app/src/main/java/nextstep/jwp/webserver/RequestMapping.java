package nextstep.jwp.webserver;

import nextstep.jwp.application.controller.HelloWorldController;
import nextstep.jwp.application.controller.LoginController;
import nextstep.jwp.application.controller.RegisterController;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private static final Map<String, Controller> router = new HashMap<>();

    static {
        add(new HelloWorldController());
        add(new LoginController());
        add(new RegisterController());
    }

    public static void add(Controller controller) {
        router.put(controller.mappingUri(), controller);
    }

    public static Controller get(String path) {
        if (!router.containsKey(path)) {
            return null;
        }
        return router.get(path);
    }
}
