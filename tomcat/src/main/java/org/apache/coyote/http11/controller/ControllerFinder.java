package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.requestLine.HttpMethod;
import org.apache.coyote.http11.request.requestLine.requestUri.ResourcePath;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.statusLine.HttpStatus;

public class ControllerFinder {

    private ControllerFinder() {
    }

    public static ResponseEntity find(final HttpRequest request) {
        final ResourcePath resourcePath = request.getResourcePath();

        if (resourcePath.isRootPath()) {
            return new Controller().getIndex();
        }

        if (request.isRequestOf(HttpMethod.POST)) {
            if (resourcePath.is("/login")) {
                return new LoginController().postLogin(request.getRequestBody());
            }
            if (resourcePath.is("/register")) {
                return new RegisterController().postRegister(request.getRequestBody());
            }
        }

        if (request.isRequestOf(HttpMethod.GET)) {
            if (resourcePath.is("/login")) {
                return new LoginController().getLogin(request);
            }
            if (resourcePath.is("/register")) {
                return new RegisterController().getRegister();
            }
        }

        return ResponseEntity.of(HttpStatus.OK, resourcePath.getResourcePath());
    }
}
