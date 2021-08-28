package nextstep.jwp.webserver;

import nextstep.jwp.application.controller.HelloWorldController;
import nextstep.jwp.application.controller.LoginController;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Router {

    private static final Map<String, Class<? extends Controller>> router = new HashMap<>();

    static {
        add("/", HelloWorldController.class);
        add("/login", LoginController.class);
    }

    private Router() {
    }

    public static void add(String path, Class<? extends Controller> controller) {
        router.put(path, controller);
    }

    public static Controller get(String path) {
        try {
            return router.get(path).getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        // todo 라우팅 실패
        return null;
    }
}
