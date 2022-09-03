package org.apache.coyote.request;

import nextstep.jwp.controller.AuthController;
import org.apache.coyote.exception.HttpException;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResourceView;

public class HttpRequestHandler {

    private final AuthController authController = new AuthController();
    private final ResourceView resourceView = new ResourceView();

    public HttpResponse handle(HttpRequest request) {
        if (request.getUri().startsWith("/login")) {
            authController.login(request);
        }
        if (request.isGet()) {
            return resourceView.findStaticResource(request.getUri());
        }
        throw new UnsupportedOperationException("Not implemented");
    }

    public HttpResponse handle(HttpException exception) {
        return resourceView.findErrorPage(exception);
    }
}
