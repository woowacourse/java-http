package org.apache.catalina;

import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.HomeController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

public class ServletContainer {

    private final Map<String, Controller> controllers;

    public ServletContainer() {
        this.controllers = Map.of(
                "/login", new LoginController(),
                "/register", new RegisterController(),
                "/", new HomeController()
        );
    }

    public void run(HttpRequest httpRequest, HttpResponse httpResponse) {
        Controller controller = controllers.get(httpRequest.getPath());
        controller.service(httpRequest, httpResponse);
    }
}
