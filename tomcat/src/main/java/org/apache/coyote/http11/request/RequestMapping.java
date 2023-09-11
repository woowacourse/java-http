package org.apache.coyote.http11.request;

import org.apache.coyote.http11.controller.*;

import java.util.Map;

import static org.apache.coyote.http11.controller.URIPath.LOGIN_URI;
import static org.apache.coyote.http11.controller.URIPath.REGISTER_URI;

public class RequestMapping {

    private static final Map<String, Controller> CONTROLLER_MAPPER = Map.of(
            REGISTER_URI, new RegisterController(),
            LOGIN_URI, new LoginController()
    );

    public Controller getController(HttpRequest request) {
        String path = request.getPath();
        Controller controller = CONTROLLER_MAPPER.get(path);
        if (controller == null) {
            return new DefaultController();
        }
        return controller;
    }
}
