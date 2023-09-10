package org.apache.catalina;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import org.apache.coyote.request.HttpRequest;

public class RequestMapping {

    private RequestMapping() {
    }

    public static Controller getController(final HttpRequest request) {
        final String path = request.getPath();

        if (path.equals("/")) {
            return new HomeController();
        }
        if (path.equals("/index.html")) {
            return new IndexController();
        }
        if (path.equals("/login")) {
            return new LoginController();
        }
        if (path.equals("/register")) {
            return new RegisterController();
        }
        return new ResourceController();
    }
}
