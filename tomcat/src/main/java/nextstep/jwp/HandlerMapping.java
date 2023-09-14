package nextstep.jwp;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.RootGetController;
import java.util.Map;

public class HandlerMapping {

    private static final Map<String, Controller> controllers =
            Map.of("/", new RootGetController(),
                    "/login", new LoginController(),
                    "/register", new RegisterController());

    public Controller findController(final String path) {
        return controllers.get(path);
    }
}
