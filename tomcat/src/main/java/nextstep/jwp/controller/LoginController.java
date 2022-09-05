package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.service.LoginService;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.StaticResource;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController implements Controller {

    private final LoginService loginService;

    public LoginController() {
        loginService = new LoginService();
    }

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        if (httpRequest.isSameMethod(HttpMethod.GET)) {
            return show(httpRequest.getSession());
        }
        if (httpRequest.isSameMethod(HttpMethod.POST)) {
            return login(httpRequest.getSession(), httpRequest.parseBodyQueryString());
        }
        return HttpResponse.found("/404.html");
    }

    public HttpResponse show(final Session session) {
        if (loginService.isAlreadyLogin(session)) {
            return HttpResponse.found("/index.html");
        }
        return HttpResponse.ok(StaticResource.path("/login.html"));
    }

    public HttpResponse login(final Session session, final Map<String, String> parameters) {
        if (loginService.isAlreadyLogin(session)) {
            return HttpResponse.found("/index.html");
        }
        try {
            final var httResponse = HttpResponse.found("/index.html");
            httResponse.setSessionId(loginService.login(parameters));
            return httResponse;
        } catch (NotFoundException | AuthenticationException e) {
            return HttpResponse.found("/401.html");
        }
    }
}
