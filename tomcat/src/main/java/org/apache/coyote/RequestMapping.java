package org.apache.coyote;

import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.LoginController;
import org.apache.coyote.controller.RegisterController;
import org.apache.coyote.controller.RootController;

public class RequestMapping {

    public Controller getController(final HttpRequest request) {
        if (request.getPath().startsWith("/login")) {
            return new LoginController();
        }
        if (request.getPath().startsWith("/register")) {
            return new RegisterController();
        }
        return new RootController();
    }
}
