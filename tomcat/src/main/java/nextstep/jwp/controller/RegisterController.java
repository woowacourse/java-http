package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Params;
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
        final Params params = request.getParamsFromBody();

        try {
            final String account = params.find("account");
            final String password = params.find("password");
            final String email = params.find("email");

            authService.register(account, password, email);
            return redirectToIndex();

        } catch (final AccountDuplicatedException e) {
            return redirect(HttpStatus.FOUND, Page.BAD_REQUEST.getPath());
        }
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return HttpResponse.ok()
                .body(Page.REGISTER.getResource());
    }
}
