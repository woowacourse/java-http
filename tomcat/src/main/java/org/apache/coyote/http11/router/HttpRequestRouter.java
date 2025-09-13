package org.apache.coyote.http11.router;

import java.io.IOException;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.HomeController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class HttpRequestRouter {
    private final RequestMapping requestMapping;

    public HttpRequestRouter(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    public void initialize() {
        requestMapping.registerMapping("/", new HomeController());
        requestMapping.registerMapping("/register", new RegisterController());
        requestMapping.registerMapping("/login", new LoginController());
    }

    public void route(final HttpRequest request, final HttpResponse response) throws IOException {
        final Controller controller = requestMapping.getController(request.getPath());

        if (controller != null) {
            controller.service(request, response);
        }
    }
}
