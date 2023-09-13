package org.apache.catalina;

import static org.apache.catalina.controller.StaticResourceUri.DEFAULT_PAGE;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.DefaultController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.RootController;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final String ROOT_REQUEST_URL = "/";
    private static final String LOGIN_REQUEST_URL = "/login";
    private static final String REGISTER_REQUEST_URL = "/register";
    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put(ROOT_REQUEST_URL, new RootController());
        controllers.put(DEFAULT_PAGE.getUri(), new DefaultController());
        controllers.put(LOGIN_REQUEST_URL, new LoginController());
        controllers.put(REGISTER_REQUEST_URL, new RegisterController());
    }

    public Controller getController(final HttpRequest request) {
        final String path = request.getUri().getPath();

        if (controllers.containsKey(path)) {
            return controllers.get(path);
        }

        return new StaticResourceController();
    }
}
