package org.apache.catalina;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.HomeController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.http11.message.HttpRequest;

public class RequestMapping {

    public Controller getController(HttpRequest request) {
        String path = request.getPath();

        if (path.equals("/")) {
            return new HomeController();
        }

        if (path.contains("/login")) {
            return new LoginController();
        }

        if (path.contains("/register")) {
            return new RegisterController();
        }

        return new StaticResourceController();
    }
}
