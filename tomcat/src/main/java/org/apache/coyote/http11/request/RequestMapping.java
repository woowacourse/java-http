package org.apache.coyote.http11.request;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.DefaultController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;

public class RequestMapping {

    private static final String REGISTER_URI = "/register";
    private static final String LOGIN_URI = "/login";

    private static final Controller LOGIN_CONTROLLER = new LoginController();
    private static final Controller REGISTER_CONTROLLER = new RegisterController();
    private static final Controller DEFAULT_CONTROLLER = new DefaultController();

    public Controller getController(HttpRequest request) {
        String path = request.getPath();
        if (path.equals(REGISTER_URI)) {
            return REGISTER_CONTROLLER;
        }

        if (path.equals(LOGIN_URI)) {
            return LOGIN_CONTROLLER;
        }

        return DEFAULT_CONTROLLER;
    }
}
