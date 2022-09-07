package org.apache.coyote.http11;

import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticResourceController;

public class RequestMapping {

    Controller findController(final HttpRequest request) {
        if (request.getUriPath().equals("/")) {
            return new HomeController();
        }

        if (request.getUriPath().equals("/login")) {
            return new LoginController();
        }

        if (request.getUriPath().equals("/register")) {
            return new RegisterController();
        }

        return new StaticResourceController();
    }
}
