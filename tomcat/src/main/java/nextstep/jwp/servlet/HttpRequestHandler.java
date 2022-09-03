package nextstep.jwp.servlet;

import nextstep.jwp.controller.AuthController;
import nextstep.jwp.support.ResourceRegistry;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.support.HttpException;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.support.HttpMethod;

public class HttpRequestHandler {

    private final AuthController authController = new AuthController();
    private final ResourceRegistry resourceRegistry = new ResourceRegistry();

    public HttpResponse handle(HttpRequest request) {
        if (request.isMethodOf(HttpMethod.GET)) {
            return resourceRegistry.findStaticResource(request.getUri());
        }
        if (request.getUri().matches("/register") && request.isMethodOf(HttpMethod.POST)) {
            return authController.register(request);
        }
        if (request.getUri().matches("/login") && request.isMethodOf(HttpMethod.POST)) {
            return authController.login(request);
        }
        throw new UnsupportedOperationException("Not implemented");
    }

    public HttpResponse handle(HttpException exception) {
        return resourceRegistry.findErrorPage(exception);
    }
}
