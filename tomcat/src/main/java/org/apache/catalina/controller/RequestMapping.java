package org.apache.catalina.controller;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import com.techcourse.controller.StaticResourceController;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    public static Controller getController(HttpRequest request) {
        if (request.getUrl().equals("/")) {
            return new RootController();
        }
        if (request.getUrl().equals("/login")) {
            return new LoginController();
        }
        if (request.getUrl().equals("/register")) {
            return new RegisterController();
        }
        return new StaticResourceController();
    }
}
