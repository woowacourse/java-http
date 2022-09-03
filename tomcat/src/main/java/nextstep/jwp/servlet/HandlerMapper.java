package nextstep.jwp.servlet;

import nextstep.jwp.controller.AuthController;
import nextstep.jwp.support.ResourceRegistry;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;
import org.apache.coyote.support.HttpMethod;

public class HandlerMapper {

    private final AuthController authController;
    private final ResourceRegistry resourceRegistry;

    public HandlerMapper(AuthController authController, ResourceRegistry resourceRegistry) {
        this.authController = authController;
        this.resourceRegistry = resourceRegistry;
    }

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
}
