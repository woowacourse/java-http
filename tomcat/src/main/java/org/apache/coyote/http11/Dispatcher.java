package org.apache.coyote.http11;

import java.util.Map;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.HomeController;
import org.apache.coyote.controller.LoginController;
import org.apache.coyote.controller.RegisterController;
import org.apache.coyote.controller.StaticController;
import org.apache.coyote.http.HeaderName;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class Dispatcher {

    private final Map<String, Controller> controllers;
    private final Controller staticController;

    public Dispatcher() {
        this.controllers = Map.of(
                "/login", new LoginController(),
                "/register", new RegisterController(),
                "/", new HomeController()
        );
        this.staticController = new StaticController();
    }

    public void run(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.addHeader(HeaderName.CONTENT_TYPE, httpRequest.getContentType());
        httpResponse.addHeader(HeaderName.SET_COOKIE, httpRequest.getHttpCookie());

        if (httpRequest.isStaticRequest()) {
            staticController.service(httpRequest, httpResponse);
        }
        if (!httpRequest.isStaticRequest()) {
            Controller controller = controllers.get(httpRequest.getPath());
            controller.service(httpRequest, httpResponse);
        }
    }
}
