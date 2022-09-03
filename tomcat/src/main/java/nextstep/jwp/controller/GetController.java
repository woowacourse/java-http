package nextstep.jwp.controller;

import nextstep.jwp.servlet.ViewResolver;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;
import org.apache.coyote.support.HttpMethod;

public class GetController {

    private final ViewResolver viewResolver;

    public GetController(ViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    @RequestMapping(method = HttpMethod.GET, path = {"/", "/index"})
    public HttpResponse home(HttpRequest request) {
        return viewResolver.findStaticResource("/index.html");
    }

    @RequestMapping(method = HttpMethod.GET, path = "/login")
    public HttpResponse login(HttpRequest request) {
        return viewResolver.findStaticResource("/login.html");
    }

    @RequestMapping(method = HttpMethod.GET, path = "/register")
    public HttpResponse register(HttpRequest request) {
        return viewResolver.findStaticResource("/register.html");
    }
}
