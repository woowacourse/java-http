package org.apache.coyote.http11;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.HomeController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.StaticResourceController;
import org.apache.coyote.http11.model.request.HttpRequest;

public class RequestMapping {

    public Controller getController(HttpRequest request) {
        String path = request.getRequestPath();
        if ("/".equals(request.getRequestPath())) {
            return new HomeController();
        }
        if ("/login".equals(request.getRequestPath())) {
            return new LoginController();
        }
        if ("/register".equals(request.getRequestPath())) {
            return new RegisterController();
        }
        if (path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js")) {
            return new StaticResourceController();
        }
        throw new IllegalArgumentException("잘못된 요청입니다.");
    }
}
