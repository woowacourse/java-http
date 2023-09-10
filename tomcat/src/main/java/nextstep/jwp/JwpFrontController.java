package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.coyote.handler.Controller;
import org.apache.coyote.handler.FrontController;

public class JwpFrontController implements FrontController {

    private final Map<String, Controller> handlerMapping = new HashMap<>();

    public JwpFrontController() {
        handlerMapping.put("/", new IndexController());
        handlerMapping.put("/login", new LoginController());
        handlerMapping.put("/register", new RegisterController());
    }

    @Override
    public Controller getHandler(final String requestUri) {
        return handlerMapping.get(requestUri);
    }
}
