package nextstep.jwp.http;

import nextstep.jwp.WebServer;
import nextstep.jwp.controller.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static final Logger logger = LoggerFactory.getLogger(RequestMapping.class);
    public static Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/", new DefaultController());
        controllers.put("/register", new RegisterController());
        controllers.put("/login", new LoginController());
    }

    public static Controller findController(String inputPath) {
        Controller controller = controllers.keySet().stream()
                .filter(path -> path.equals(inputPath))
                .findAny()
                .map(path -> controllers.get(path))
                .orElseGet(ResourceController::new);
        logger.info("Found {}", controller.getClass());
        return controller;
    }
}
