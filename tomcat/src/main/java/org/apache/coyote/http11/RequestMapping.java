package org.apache.coyote.http11;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.HomeController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.StaticRequestController;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final RequestMapping INSTANCE = new RequestMapping();

    public static RequestMapping getInstance() {
        return INSTANCE;
    }

    public Controller getController(final HttpRequest request) {
        if (request.isPathEqualsTo("/")) {
            return new HomeController();
        }
        if (request.isPathEqualsTo("/register")) {
            return new RegisterController();
        }
        if (request.isPathEqualsTo("/login")) {
            return new LoginController();
        }
        return new StaticRequestController();
    }

    private RequestMapping() {
    }
}
