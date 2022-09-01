package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.DuplicationException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.service.Service;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.StaticResource;
import org.apache.coyote.http11.response.HttpResponse;

public class Controller {

    private final Service service;

    public Controller() {
        service = new Service();
    }

    public HttpResponse showIndex() {
        return HttpResponse.ok(new StaticResource("Hello world!", ContentType.TEXT_HTML));
    }

    public HttpResponse showLogin() {
        return HttpResponse.ok(StaticResource.path("/login.html"));
    }

    public HttpResponse login(final Map<String, String> parameters) {
        try {
            service.login(parameters);
            return HttpResponse.found("/index.html");
        } catch (NotFoundException | AuthenticationException e) {
            return HttpResponse.found("/401.html");
        }
    }

    public HttpResponse showRegister() {
        return HttpResponse.ok(StaticResource.path("/register.html"));
    }

    public HttpResponse register(final Map<String, String> parameters) {
        try {
            service.register(parameters);
            return HttpResponse.found("/index.html");
        } catch (DuplicationException e) {
            return HttpResponse.found("/register.html");
        }
    }

    public HttpResponse show(final String path) {
        try {
            return HttpResponse.ok(StaticResource.path(path));
        } catch (NotFoundException e) {
            return HttpResponse.found("/404.html");
        }
    }
}
