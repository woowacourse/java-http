package nextstep.jwp.controller;

import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.service.LoginService;
import org.apache.coyote.http11.common.StaticResource;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private final LoginService loginService;

    public LoginController() {
        loginService = new LoginService();
    }

    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final var session = httpRequest.getSession();

        if (loginService.isAlreadyLogin(session)) {
            httpResponse.found("/index.html");
            return;
        }
        httpResponse.ok(StaticResource.path("/login.html"));
    }

    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final var session = httpRequest.getSession();
        final var parameters = httpRequest.parseBodyQueryString();

        if (loginService.isAlreadyLogin(session)) {
            httpResponse.found("/index.html");
            return;
        }
        try {
            httpResponse.setSessionId(loginService.login(parameters));
            httpResponse.found("/index.html");
        } catch (AuthenticationException e) {
            httpResponse.found("/401.html");
        }
    }
}
