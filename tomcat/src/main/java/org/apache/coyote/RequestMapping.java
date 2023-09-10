package org.apache.coyote;

import nextstep.jwp.controller.DefaultController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.RootController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestUri;

public class RequestMapping {

    public Controller getController(HttpRequest request) {
        RequestUri requestUri = request.getRequestLine().getRequestUri();

        String path = requestUri.getPath();
        if (path.equals("/")) {
            return new RootController();
        }
        if (path.equals("/login")) {
            return new LoginController();
        }
        if (path.equals("/register")) {
            return new RegisterController();
        }
        return new DefaultController();
    }
}
