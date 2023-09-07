package nextstep.jwp.presentation;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class FrontController {

    private static final Map<String, Controller> controllers = new HashMap<>();
    private static final FrontController frontController = new FrontController();

    static {
        controllers.put("/", MainPageController.getInstance());
        controllers.put("/login", LoginController.getInstance());
        controllers.put("/register", RegisterController.getInstance());
    }

    private FrontController() {
    }

    public static FrontController getInstance() {
        return frontController;
    }

    public Controller findController(HttpRequest httpRequest) {
        return controllers.getOrDefault(httpRequest.getRequestUrl(), ResourceController.getInstance());
    }
}
