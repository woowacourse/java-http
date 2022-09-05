package org.apache.coyote.http11;

import nextstep.jwp.ui.IndexController;
import nextstep.jwp.ui.LoginController;
import nextstep.jwp.ui.ResourceController;
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
        return ResourceController.getController();
    }
}
