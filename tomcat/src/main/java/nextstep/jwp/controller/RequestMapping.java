package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    public Controller getController(HttpRequest request) {
        final String path = request.getPath();

        if (path.equals("/")) {
            return new IndexController();
        }
        if (path.equals("/login")) {
            return new LoginController();
        }
        if (path.equals("/register")) {
            return new RegisterController();
        }
        return new ResourceController();
    }
}
