package nextstep.jwp;

import nextstep.jwp.controller.*;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    public static Map<String, Controller> controllers = new HashMap<String, Controller>();

    static {
        controllers.put("/", new DefaultController());
        controllers.put("/register", new RegisterController());
        controllers.put("/login", new LoginController());
    }

    public static Controller findController(String inputPath) {
        return controllers.keySet().stream()
                .filter(path -> path.equals(inputPath))
                .findAny()
                .map(path -> controllers.get(path))
                .orElseGet(ResourceController::new);
    }
}
