package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticFileController;
import org.apache.catalina.exception.ControllerNotFoundException;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.URL;

public class RequestMapping {

    private static final Map<URL, Controller> REQUEST_MAP = new HashMap<>();
    private static final Controller STATIC_CONTROLLER = new StaticFileController();

    static {
        REQUEST_MAP.put(URL.of("/login"), new LoginController());
        REQUEST_MAP.put(URL.of("/register"), new RegisterController());
    }

    public Controller map(final String urlValue) {
        return map(URL.of(urlValue));
    }

    public Controller map(final URL url) {
        final Controller controller = REQUEST_MAP.get(url);
        if (controller == null) {
            return getStaticController(url);
        }
        return controller;
    }

    private Controller getStaticController(final URL url) {
        if (url.isDefault() || url.isForStaticFile()) {
            return STATIC_CONTROLLER;
        }
        throw new ControllerNotFoundException();
    }
}
