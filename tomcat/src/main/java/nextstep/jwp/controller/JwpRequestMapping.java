package nextstep.jwp.controller;

import java.util.Map;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;

public class JwpRequestMapping implements RequestMapping {

    private static final Map<String, Controller> CONTROLLERS = Map.of(
            "/", new HomeController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    @Override
    public Controller getController(final HttpRequest request) {
        if (CONTROLLERS.containsKey(request.getUriPath())) {
            return CONTROLLERS.get(request.getUriPath());
        }

        return new StaticResourceController();
    }
}
