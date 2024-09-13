package org.apache.catalina.controller;

import com.techcourse.controller.IndexController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Arrays;

public enum RequestMapping {

    INDEX("/", new IndexController()),
    LOGIN("/login", new LoginController()),
    REGISTER("/register", new RegisterController()),
    ;

    private final String path;
    private final Controller controller;

    RequestMapping(String path, Controller controller) {
        this.path = path;
        this.controller = controller;
    }

    public static Controller getController(String path) {
        return Arrays.stream(values())
                .filter(mapper -> isPathMatched(mapper, path))
                .map(mapper -> mapper.controller)
                .findFirst()
                .orElse(StaticResourceController.INSTANCE);
    }

    private static boolean isPathMatched(RequestMapping requestMapping, String path) {
        return requestMapping.path.equals(path);
    }
}
