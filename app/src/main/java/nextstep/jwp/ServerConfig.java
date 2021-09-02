package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.handler.controller.AbstractController;
import nextstep.jwp.handler.controller.LoginController;
import nextstep.jwp.handler.controller.MainController;
import nextstep.jwp.handler.controller.RegisterController;

public class ServerConfig {
    public static final int DEFAULT_PORT = 8080;
    public static final String RESOURCE_BASE_PATH = "/static";
    public static final String ROOT_RESPONSE = "Hello world!";

    public static final Map<String, Class<? extends AbstractController>> CONTROLLER_MAP = new HashMap<>();

    static {
        CONTROLLER_MAP.put("/login", LoginController.class);
        CONTROLLER_MAP.put("/register", RegisterController.class);
        CONTROLLER_MAP.put("/index", MainController.class);
        CONTROLLER_MAP.put("/index.html", MainController.class);
    }

    private ServerConfig() { }
}
