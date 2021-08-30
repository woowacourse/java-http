package nextstep.jwp.controller;

import nextstep.jwp.model.HttpRequest;

public class RequestMapping {

    public static Controller getController(HttpRequest httpRequest) {
        String path = httpRequest.getUri();
        if ("/".equals(path)) {
            return new DefaultController();
        }
        if ("/login".equals(path)) {
            return new LoginController();
        }
        if ("/register".equals(path)) {
            return new RegisterController();
        }
        return new IndexController();
    }
}
