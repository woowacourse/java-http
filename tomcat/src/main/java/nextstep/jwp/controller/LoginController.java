package nextstep.jwp.controller;

import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.catalina.session.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Params;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    @Override
    protected final HttpResponse doPost(HttpRequest request) {
        final Params params = request.getParamsFromBody();

        if (request.existSession()) {
            return redirectToIndex();
        }

        try {
            final String account = params.find("account");
            final String password = params.find("password");

            final User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(UserNotFoundException::new);

            if (user.checkPassword(password)) {
                final Session session = request.generateSession(Map.of("user", user));

                return HttpResponse.found()
                        .setCookie(session)
                        .location("/index.html");
            }

            return fail(HttpStatus.UNAUTHORIZED, Page.UNAUTHORIZED);

        } catch (final UserNotFoundException e) {
            return fail(HttpStatus.UNAUTHORIZED, Page.UNAUTHORIZED);

        } catch (final NoSuchElementException e) {
            return fail(HttpStatus.BAD_REQUEST, Page.BAD_REQUEST);
        }
    }

    @Override
    protected final HttpResponse doGet(HttpRequest request) {
        if (request.existSession()) {
            return redirectToIndex();
       }
        return success(HttpStatus.OK, Page.LOGIN);
    }
}
