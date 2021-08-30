package nextstep.jwp.http;

import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;

public class RequestMapper {

    public static AbstractController map(HttpRequest httpRequest) {
        if (httpRequest.uri().equals("/") || httpRequest.uri().equals("/index.html")) {
            return new HomeController(httpRequest);
        }
        if (httpRequest.uri().startsWith("/login")) {
            return new LoginController(httpRequest);
        }
        return new Controller(httpRequest);
    }
}
