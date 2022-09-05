package org.apache.coyote.core;

import nextstep.jwp.http.reqeust.HttpRequest;
import org.apache.coyote.core.controller.Controller;
import org.apache.coyote.core.controller.ExceptionController;
import org.apache.coyote.core.controller.IndexController;
import org.apache.coyote.core.controller.LoginController;
import org.apache.coyote.core.controller.RegisterController;
import org.apache.coyote.core.controller.RootController;

public class RequestMapping {

    public Controller getController(final HttpRequest request) {
        String path = request.getPath();
        if (path.equals("/")) {
            return new RootController();
        }
        if (path.equals("/index.html") || path.equals("/css/styles.css")) {
            return new IndexController();
        }
        if (path.equals("/login.html")) {
            return new LoginController();
        }
        if (path.equals("/401.html")) {
            return new ExceptionController();
        }
        if (path.equals("register.html")) {
            return new RegisterController();
        }
        throw new IllegalArgumentException();
    }
}
