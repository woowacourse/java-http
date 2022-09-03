package nextstep.jwp.controller;

import nextstep.jwp.servlet.RequestMapping;
import nextstep.jwp.servlet.ViewResolver;
import org.apache.coyote.servlet.response.HttpResponse;
import org.apache.coyote.support.HttpMethod;

public class GetController {

    private final ViewResolver viewResolver;

    public GetController(ViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    @RequestMapping(method = HttpMethod.GET, path = {"/", "/index"})
    public HttpResponse home() {
        return viewResolver.findStaticResource("/index.html");
    }

    @RequestMapping(method = HttpMethod.GET, path = "/login")
    public HttpResponse login() {
        return viewResolver.findStaticResource("/login.html");
    }

    @RequestMapping(method = HttpMethod.GET, path = "/register")
    public HttpResponse register() {
        return viewResolver.findStaticResource("/register.html");
    }
}
