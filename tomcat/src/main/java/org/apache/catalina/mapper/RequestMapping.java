package org.apache.catalina.mapper;

import org.apache.catalina.controller.BaseController;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.coyote.http11.request.Request;

public class RequestMapping {

    private RequestMapping() {}

    public static Controller getController(final Request request) {
        final String path = request.getRequestLine().getPath();
        if (path.equals("/login")) {
            return new LoginController();
        }
        if (path.equals("/register")) {
            return new RegisterController();
        }
        return new BaseController();
    }
}
