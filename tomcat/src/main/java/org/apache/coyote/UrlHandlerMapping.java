package org.apache.coyote;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.controller.FileController;
import org.apache.coyote.http11.controller.HelloController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.message.request.HttpRequest;

public class UrlHandlerMapping {

    private static final Map<String, Controller> urlMatchedController;
    private static final FileController fileController = new FileController();

    static {
        urlMatchedController = new LinkedHashMap<>(
            Map.of(
                "/", new HelloController(),
                "/login", new LoginController(),
                "/register", new RegisterController()
            )
        );
    }

    private UrlHandlerMapping() {}

    public static Controller getHandler(final HttpRequest httpRequest) {
        return urlMatchedController.getOrDefault(httpRequest.getPath(), fileController);
    }
}
