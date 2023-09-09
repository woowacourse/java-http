package org.apache.catalina;

import nextstep.jwp.DefaultController;
import nextstep.jwp.HomeController;
import nextstep.jwp.LoginController;
import nextstep.jwp.RegisterController;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    public Controller getController(final HttpRequest request) {
        final String resource = request.getRequestPath().getResource();

        if (resource.equals("/")) {
            return new HomeController();
        }
        if (resource.equals("/login")) {
            return new LoginController();
        }
        if (resource.equals("/register")) {
            return new RegisterController();
        }

        return new DefaultController();
    }
}
