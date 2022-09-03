package nextstep.jwp.controller;

import nextstep.jwp.support.ResourceRegistry;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;

public class GetController {

    private final ResourceRegistry resourceRegistry;

    public GetController(ResourceRegistry resourceRegistry) {
        this.resourceRegistry = resourceRegistry;
    }

    public HttpResponse home() {
        return resourceRegistry.findStaticResource("/index.html");
    }

    public HttpResponse login() {
        return resourceRegistry.findStaticResource("/login.html");
    }

    public HttpResponse register() {
        return resourceRegistry.findStaticResource("/register.html");
    }
}
