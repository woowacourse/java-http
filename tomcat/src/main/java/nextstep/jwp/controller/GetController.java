package nextstep.jwp.controller;

import nextstep.jwp.support.ResourceRegistry;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;
import org.apache.coyote.support.HttpMethod;

public class GetController {

    private final ResourceRegistry resourceRegistry;

    public GetController(ResourceRegistry resourceRegistry) {
        this.resourceRegistry = resourceRegistry;
    }

    @RequestMapping(method = HttpMethod.GET, path = {"/", "/index"})
    public HttpResponse home(HttpRequest request) {
        return resourceRegistry.findStaticResource("/index.html");
    }

    @RequestMapping(method = HttpMethod.GET, path = "/login")
    public HttpResponse login(HttpRequest request) {
        return resourceRegistry.findStaticResource("/login.html");
    }

    @RequestMapping(method = HttpMethod.GET, path = "/register")
    public HttpResponse register(HttpRequest request) {
        return resourceRegistry.findStaticResource("/register.html");
    }
}
