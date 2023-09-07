package org.apache.coyote.handler;

import org.apache.coyote.handler.controller.HomePageController;
import org.apache.coyote.handler.controller.StaticFileController;
import org.apache.coyote.handler.controller.login.LoginController;
import org.apache.coyote.handler.controller.login.LoginPageController;
import org.apache.coyote.handler.controller.register.RegisterController;
import org.apache.coyote.handler.controller.register.RegisterPageController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

import java.util.HashSet;
import java.util.Set;

import static org.apache.coyote.handler.controller.Path.NOT_FOUND;

public class FrontHandler {

    private static final Set<RequestController> requestControllers = new HashSet<>();

    static {
        requestControllers.add(new HomePageController());
        requestControllers.add(new StaticFileController());
        requestControllers.add(new LoginController());
        requestControllers.add(new LoginPageController());
        requestControllers.add(new RegisterController());
        requestControllers.add(new RegisterPageController());
    }

    public void handle(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        for (final RequestController mapping : requestControllers) {
            if (mapping.supports(httpRequest)) {
                mapping.service(httpRequest, httpResponse);
                return;
            }
        }

        httpResponse.mapToRedirect(NOT_FOUND.getPath());
    }
}
