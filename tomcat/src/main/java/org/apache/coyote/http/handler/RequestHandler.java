package org.apache.coyote.http.handler;

import java.util.Map;
import org.apache.coyote.http.controller.Controller;
import org.apache.coyote.http.controller.HomeController;
import org.apache.coyote.http.controller.LoginController;
import org.apache.coyote.http.controller.RegisterController;
import org.apache.coyote.http.controller.StaticFileController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class RequestHandler {

    private final Map<String, Controller> controllers;

    public RequestHandler() {
        this.controllers = Map.of(
                "/", new HomeController(),
                "/css/styles.css", new StaticFileController("/css/styles.css", "text/css"),
                "/login", new LoginController(),
                "/register", new RegisterController()
        );
    }

    public HttpResponse handleRequest(HttpRequest request) {
        try {
            String endpoint = request.getEndpoint();
            Controller controller = controllers.get(endpoint);

            if (controller != null) {
                return controller.service(request);
            }

            return new StaticFileController(endpoint, "text/html").service(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
