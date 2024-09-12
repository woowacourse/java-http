package org.apache.coyote.http11;

import java.util.Map;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RootController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);

    private final Map<String, Controller> controllerResolver;

    public HttpRequestHandler() {
        this.controllerResolver = Map.of(
                "/", new RootController(),
                "/login", new LoginController(),
                "/register", new RegisterController()
        );
    }

    public void handle(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        Controller controller = controllerResolver.get(path);
        if (controller != null) {
            controller.service(request, response);
        }

        if (controller == null) {
            response.statusCode(HttpStatusCode.OK)
                    .staticResource(path);
        }
    }
}
