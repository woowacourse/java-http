package org.apache.coyote.http11;

import java.util.Map;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticResourceController;

class RequestMapping {

    private static final Map<String, Controller> CONTROLLERS = Map.of(
            "/", new HomeController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    private RequestMapping() {
        throw new UnsupportedOperationException("RequestMapping 객체를 생성할 수 없습니다.");
    }

    static Controller getController(final HttpRequest request) {
        if (CONTROLLERS.containsKey(request.getUriPath())) {
            return CONTROLLERS.get(request.getUriPath());
        }

        return new StaticResourceController();
    }
}
