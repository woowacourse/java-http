package org.apache.coyote;

import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.LoginController;
import org.apache.coyote.controller.RegisterController;
import org.apache.coyote.controller.RootController;

public class RequestMapping {

    private static final RootController rootController = new RootController();
    private static final LoginController loginController = new LoginController();
    private static final RegisterController registerController = new RegisterController();

    public Controller getController(final HttpRequest request) {
        if (request.getPath().startsWith("/login")) {
            return loginController;
        }
        if (request.getPath().startsWith("/register")) {
            return registerController;
        }
        return rootController;
    }
}
