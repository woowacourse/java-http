package nextstep.jwp.controller;

import org.apache.catalina.session.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Params;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import nextstep.jwp.exception.LoginFailedException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.service.AuthService;

public class LoginController extends AbstractController {

    private final AuthService authService;

    public LoginController(final AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected final HttpResponse doPost(final HttpRequest request) {
        final Params params = request.getParamsFromBody();

        if (request.existSession()) {
            return redirectToIndex();
        }

        try {
            final String account = params.find("account");
            final String password = params.find("password");

            final Session session = authService.login(account, password);

            return HttpResponse.found()
                    .setCookie(session)
                    .location("/index.html");

        } catch (final UserNotFoundException | LoginFailedException e) {
            return fail(HttpStatus.UNAUTHORIZED, Page.UNAUTHORIZED);
        }
    }

    @Override
    protected final HttpResponse doGet(final HttpRequest request) {
        if (request.existSession()) {
            return redirectToIndex();
        }
        return success(HttpStatus.OK, Page.LOGIN);
    }
}
