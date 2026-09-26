package org.apache.coyote.http11;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;

public class RequestMapping {

    public Controller getController(HttpRequest request) {
        if (request.getPath().equals("/login")) {
            return new LoginController();
        }
        return new RegisterController();
    }
}
