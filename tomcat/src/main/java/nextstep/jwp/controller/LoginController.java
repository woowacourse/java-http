package nextstep.jwp.controller;

import java.util.function.Supplier;

import org.apache.catalina.session.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestCookie;
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
        return ifSessionNotExist(request, () -> {
            try {
                final String account = request.findParamFromBody("account");
                final String password = request.findParamFromBody("password");

                final Session session = authService.login(account, password);

                return HttpResponse.found()
                        .setCookie(session)
                        .location("/index.html");

            } catch (final UserNotFoundException | LoginFailedException e) {
                return fail(HttpStatus.UNAUTHORIZED, Page.UNAUTHORIZED);
            }
        });
    }

    @Override
    protected final HttpResponse doGet(final HttpRequest request) {
        return ifSessionNotExist(request, () ->
                success(HttpStatus.OK, Page.LOGIN)
        );
    }

    private HttpResponse ifSessionNotExist(final HttpRequest request,
                                           final Supplier<HttpResponse> supplier) {
        final RequestCookie cookie = RequestCookie.parse(request.getHeader());
        if (cookie.existSession()) {
            return redirectToIndex();
        }
        return supplier.get();
    }
}
