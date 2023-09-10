package org.apache.coyote.http11.step1Controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.requestLine.HttpMethod;
import org.apache.coyote.http11.request.requestLine.requestUri.ResourcePath;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.statusLine.HttpStatus;

public class Step1ControllerFinder {

    private Step1ControllerFinder() {
    }

    public static ResponseEntity find(final HttpRequest request) {
        final ResourcePath resourcePath = request.getResourcePath();

        if (resourcePath.isRootPath()) {
            return new Step1Controller().getIndex();
        }

        if (request.isRequestOf(HttpMethod.POST)) {
            if (resourcePath.is("/login")) {
                return new Step1LoginController().postLogin(request.getRequestBody());
            }
            if (resourcePath.is("/register")) {
                return new Step1RegisterController().postRegister(request.getRequestBody());
            }
        }

        if (request.isRequestOf(HttpMethod.GET)) {
            if (resourcePath.is("/login")) {
                return new Step1LoginController().getLogin(request);
            }
            if (resourcePath.is("/register")) {
                return new Step1RegisterController().getRegister();
            }
        }

        return ResponseEntity.of(HttpStatus.OK, resourcePath.getResourcePath());
    }
}
