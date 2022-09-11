package nextstep.jwp.controller;

import java.util.function.Function;

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
        return ifSessionNotExist(request, params -> {
            try {
                final String account = params.find("account");
                final String password = params.find("password");
                final String email = params.find("email");

                authService.register(account, password, email);
                return redirectToIndex();

            } catch (final AccountDuplicatedException e) {
                return redirect(HttpStatus.FOUND, Page.BAD_REQUEST.getPath());
            }
        });
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return ifSessionNotExist(request, params ->
                success(HttpStatus.OK, Page.REGISTER)
        );
    }

    private HttpResponse ifSessionNotExist(final HttpRequest request,
                                           final Function<Params, HttpResponse> function) {
        if (request.existSession()) {
            return redirectToIndex();
        }
        return function.apply(request.getParamsFromBody());
    }
}
