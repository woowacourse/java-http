package nextstep.jwp.servlet;

import nextstep.jwp.controller.PostController;
import nextstep.jwp.controller.GetController;
import nextstep.jwp.controller.ResourceController;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;
import org.apache.coyote.support.HttpMethod;

public class HandlerMapper {

    private final GetController getController;
    private final PostController postController;
    private final ResourceController resourceController;

    public HandlerMapper(GetController getController,
                         PostController postController,
                         ResourceController resourceController) {
        this.getController = getController;
        this.postController = postController;
        this.resourceController = resourceController;
    }

    public HttpResponse handle(HttpRequest request) {
        String uri = request.getUri();
        if (request.isMethodOf(HttpMethod.GET)) {
            if (uri.matches("/") || uri.matches("/index")) {
                return getController.home();
            }
            if (uri.matches("/login")) {
                return getController.login();
            }
            if (uri.matches("/register")) {
                return getController.register();
            }
            return resourceController.find(request);
        }
        if (request.isMethodOf(HttpMethod.POST)) {
            if (uri.matches("/login")) {
                return postController.login(request);
            }
            if (uri.matches("/register")) {
                return postController.register(request);
            }
        }
        throw new UnsupportedOperationException("Not implemented");
    }
}
