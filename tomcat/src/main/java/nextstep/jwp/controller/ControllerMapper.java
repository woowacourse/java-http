package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {

    private final static Map<String, Handler> HANDLER_MAP = new HashMap<>();

    static {
        HANDLER_MAP.put("/", RootController.getINSTANCE());
        HANDLER_MAP.put("/login", LoginController.getINSTANCE());
        HANDLER_MAP.put("/register", RegisterController.getINSTANCE());
    }

    private ControllerMapper() {
    }

    public static Handler findController(final String path) {
        return HANDLER_MAP.getOrDefault(path, ResourceController.getINSTANCE());
    }
}
