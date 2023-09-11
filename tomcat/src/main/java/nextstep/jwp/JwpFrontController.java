package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticController;
import org.apache.coyote.handler.Controller;
import org.apache.coyote.handler.FrontController;

public class JwpFrontController implements FrontController {

    private static final String STATIC_RESOURCE_KEY = "static";
    private static final String EXTENSION_DELIMITER = ".";

    private final Map<String, Controller> handlerMapping = new HashMap<>();

    public JwpFrontController() {
        handlerMapping.put(STATIC_RESOURCE_KEY, new StaticController());
        handlerMapping.put("/", new IndexController());
        handlerMapping.put("/login", new LoginController());
        handlerMapping.put("/register", new RegisterController());
    }

    @Override
    public Controller getHandler(final String requestUri) {
        if (isFileRequest(requestUri)) {
            return handlerMapping.get(STATIC_RESOURCE_KEY);
        }
        return handlerMapping.get(requestUri);
    }

    private boolean isFileRequest(final String requestUri) {
        return requestUri.contains(EXTENSION_DELIMITER);
    }
}
