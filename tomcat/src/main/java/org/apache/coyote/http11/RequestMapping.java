package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.DefaultController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.coyote.Controller;

public class RequestMapping {

    private static final Map<String, Controller> mapping = new HashMap<>();

    static {
        mapping.put("/", DefaultController.getInstance());
        mapping.put("/login", LoginController.getInstance());
        mapping.put("/register", RegisterController.getInstance());
    }

    public RequestMapping() {
    }

    public Controller findMappedController(final String requestUri) {
        return mapping.get(requestUri);
    }
}
