package nextstep.jwp.controller;

import java.util.function.Supplier;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestCookie;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import nextstep.jwp.exception.AccountDuplicatedException;
import nextstep.jwp.service.AuthService;

public class RegisterController extends AbstractController {

    private final AuthService authService;

    public RegisterController(final AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        return ifSessionNotExist(request, () -> {
            try {
                final String account = request.findParamFromBody("account");
                final String password = request.findParamFromBody("password");
                final String email = request.findParamFromBody("email");

                authService.register(account, password, email);
                return redirectToIndex();

            } catch (final AccountDuplicatedException e) {
                return redirect(HttpStatus.FOUND, Page.BAD_REQUEST.getPath());
            }
        });
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return ifSessionNotExist(request, () ->
                success(HttpStatus.OK, Page.REGISTER)
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
