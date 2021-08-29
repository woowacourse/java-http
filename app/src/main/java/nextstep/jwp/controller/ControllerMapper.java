package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {

    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/login", new LoginController());
    }

    private ControllerMapper() {
    }

    public static Controller getControllerByUrl(final String url) {
        return controllers.get(url);
    }
}
