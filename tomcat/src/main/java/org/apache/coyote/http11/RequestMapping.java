package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.DefaultController;
import nextstep.jwp.controller.ErrorController;
import nextstep.jwp.controller.FileController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;

public class RequestMapping {

    private static final String FILE_URL_FORMAT = ".";

    private static final Map<String, Controller> values = new HashMap<>();

    static {
        values.put("/", DefaultController.getInstance());
        values.put("/login", LoginController.getInstance());
        values.put("/register", RegisterController.getInstance());
    }

    private RequestMapping() {
    }

    public static Controller of(final String url) {
        if (url.contains(FILE_URL_FORMAT)) {
            return FileController.getInstance();
        }

        return values.getOrDefault(url, ErrorController.getInstance());
    }
}
