package org.apache.coyote.http11;

import nextstep.jwp.ui.Controller;
import nextstep.jwp.ui.IndexController;
import nextstep.jwp.ui.LoginController;
import nextstep.jwp.ui.NotFoundController;
import nextstep.jwp.ui.RegisterController;

public class RequestMapping {
    public static Controller getController(String requestPath) {
        if (requestPath.startsWith("/index")) {
            return IndexController.getController();
        }
        if (requestPath.startsWith("/login")) {
            return LoginController.getController();
        }
        if (requestPath.startsWith("/register")) {
            return RegisterController.getController();
        }
        return NotFoundController.getController();
    }
}
