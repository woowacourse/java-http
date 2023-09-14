package org.apache.catalina.core;

import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import org.apache.catalina.servlet.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class MappingHandler {

    private MappingHandler() {
    }

    public static Controller getController(final HttpRequest httpRequest) {
        final String path = httpRequest.getPath().toString();

        if (path.contains("/login")) {
            return new LoginController();
        }
        if (path.contains("/register")) {
            return new RegisterController();
        }

        return new ResourceController();
    }
}
