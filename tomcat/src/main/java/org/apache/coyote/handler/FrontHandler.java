package org.apache.coyote.handler;

import org.apache.coyote.handler.mapping.HomePageMapping;
import org.apache.coyote.handler.mapping.StaticFileMapping;
import org.apache.coyote.handler.mapping.login.LoginMapping;
import org.apache.coyote.handler.mapping.login.LoginPageMapping;
import org.apache.coyote.handler.mapping.register.RegisterMapping;
import org.apache.coyote.handler.mapping.register.RegisterPageMapping;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

import java.util.HashSet;
import java.util.Set;

import static org.apache.coyote.handler.mapping.Path.NOT_FOUND;

public class FrontHandler {

    private static final Set<AbstractController> requestControllers = new HashSet<>();

    static {
        requestControllers.add(new HomePageMapping());
        requestControllers.add(new StaticFileMapping());
        requestControllers.add(new LoginMapping());
        requestControllers.add(new LoginPageMapping());
        requestControllers.add(new RegisterMapping());
        requestControllers.add(new RegisterPageMapping());
    }

    public void handle(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        for (final AbstractController mapping : requestControllers) {
            if (mapping.supports(httpRequest)) {
                mapping.service(httpRequest, httpResponse);
                return;
            }
        }

        httpResponse.mapToRedirect(NOT_FOUND.getPath());
    }
}
