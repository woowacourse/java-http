package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginController implements Controller {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        if (hasLoginRelatedParams(request)) {
            final String account = request.getRequestParam("account");
            final String password = request.getRequestParam("password");

            return handleLoginProcess(account, password);
        }

        return HttpResponse.ok().fileBody("/login.html").build();
    }

    private boolean hasLoginRelatedParams(HttpRequest request) {
        return request.getRequestParam("account") != null &&
                request.getRequestParam("password") != null;
    }

    private HttpResponse handleLoginProcess(final String account, final String password) {
        try {
            return login(account, password);
        } catch (UnauthorizedException e) {
            return HttpResponse.redirect("/401.html").build();
        }
    }

    private HttpResponse login(final String account, String password) {
        final User user = InMemoryUserRepository
                .findByAccount(account)
                .orElseThrow(UnauthorizedException::new);

        if (user.checkPassword(password)) {
            return HttpResponse.redirect("/index.html").build();
        }

        throw new UnauthorizedException();
    }
}
