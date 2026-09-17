package com.techcourse.web;

import com.techcourse.controller.ApplicationController;
import com.techcourse.controller.ControllerResult.Redirect;
import com.techcourse.controller.ControllerResult.View;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.coyote.Dispatcher;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public class ApplicationDispatcher implements Dispatcher {

    private final ApplicationController applicationController;
    private final StaticResourceHandler staticResourceHandler;

    public ApplicationDispatcher(ApplicationController applicationController, StaticResourceHandler staticResourceHandler) {
        this.applicationController = applicationController;
        this.staticResourceHandler = staticResourceHandler;
    }

    @Override
    public HttpResponse dispatch(HttpRequest httpRequest, Session session) throws IOException {
        String path = httpRequest.path();
        Map<String, String> parameters = httpRequest.parameters();

        if ("/login".equals(path)) {
            return switch (applicationController.login(parameters, session)) {
                case View view -> staticResourceHandler.createResponse(view.path());
                case Redirect redirect -> HttpResponse.redirect(redirect.location());
            };
        }

        if ("/register".equals(path)) {
            return switch (applicationController.register(parameters)) {
                case View view -> staticResourceHandler.createResponse(view.path());
                case Redirect redirect -> HttpResponse.redirect(redirect.location());
            };
        }

        if ("/".equals(path)) {
            return HttpResponse.ok(
                    "text/html;charset=utf-8",
                    "Hello world!".getBytes(StandardCharsets.UTF_8)
            );
        }

        return staticResourceHandler.createResponse(path);
    }

}
