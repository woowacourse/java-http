package org.apache.catalina.controller;

import com.techcourse.controller.IndexController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Arrays;
import org.apache.coyote.http.request.HttpRequest;

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

    public static Controller getController(HttpRequest request) {
        return Arrays.stream(values())
                .filter(requestMapping -> requestMapping.path.equals(request.getUri()))
                .map(requestMapping -> requestMapping.controller)
                .findFirst()
                .orElse(StaticResourceController.INSTANCE);
    }
}
