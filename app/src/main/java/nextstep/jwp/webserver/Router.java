package nextstep.jwp.webserver;

import nextstep.jwp.application.controller.HelloWorldController;
import nextstep.jwp.application.controller.LoginController;

import java.util.HashMap;
import java.util.Map;

public class Router {

    private static final Map<String, Controller> router = new HashMap<>();

    static {
        add("/", new HelloWorldController());
        add("/login", new LoginController());
    }

    private Router() {
    }

    public static void add(String path, Controller controller) {
        router.put(path, controller);
    }

    public static Controller get(String path) {
        if (!router.containsKey(path)) {
            return null;
        }
        return router.get(path);
    }
}
