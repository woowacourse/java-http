package org.apache.catalina;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.PageController;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private RequestMapping() {
    }

    public static Controller getController(HttpRequest request) {
        if (request.getPath().startsWith("/login")) {
            return new LoginController();
        } else if (request.getPath().startsWith("/register")) {
            return new RegisterController();
        } else {
            return new PageController();
        }
    }
}
