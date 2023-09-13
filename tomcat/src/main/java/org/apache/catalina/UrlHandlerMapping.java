package org.apache.catalina;

import java.util.LinkedHashMap;
import java.util.Map;
import nextstep.jwp.controller.HelloController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.FileController;
import org.apache.coyote.http11.message.request.HttpRequest;

public class UrlHandlerMapping {

    private static final Map<String, Controller> URL_HANDLER_MAPPING;
    private static final FileController FILE_CONTROLLER = new FileController();

    static {
        URL_HANDLER_MAPPING = new LinkedHashMap<>(
            Map.of(
                "/", new HelloController(),
                "/login", new LoginController(),
                "/register", new RegisterController()
            )
        );
    }

    private UrlHandlerMapping() {}

    public static Controller getHandler(final HttpRequest httpRequest) {
        return URL_HANDLER_MAPPING.getOrDefault(httpRequest.getPath(), FILE_CONTROLLER);
    }
}
