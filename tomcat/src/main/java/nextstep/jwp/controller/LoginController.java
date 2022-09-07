package nextstep.jwp.controller;

import java.util.NoSuchElementException;

import org.apache.catalina.session.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Params;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Resource;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        final Params params = request.getParamsFromBody();

        if (request.existSession()) {
            return redirect("/index.html");
        }

        try {
            final String account = params.find("account");
            final String password = params.find("password");

            final User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(UserNotFoundException::new);

            if (user.checkPassword(password)) {
                final Session session = request.generateSession();
                session.setAttribute("user", user);

                return new HttpResponse()
                        .status(HttpStatus.FOUND)
                        .setCookie(session)
                        .location("/index.html");
            }

            return redirect("/401.html");

        } catch (final UserNotFoundException e) {
            return redirect("/401.html");

        } catch (final NoSuchElementException e) {
            return redirect("/404.html");
        }
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        if (request.existSession()) {
            return redirect("/index.html");
        }
        return success("login.html");
    }

    private HttpResponse success(final String filePath) {
        return new HttpResponse()
                .status(HttpStatus.OK)
                .body(new Resource(filePath));
    }

    private HttpResponse redirect(final String filePath) {
        return new HttpResponse()
                .status(HttpStatus.FOUND)
                .location(filePath);
    }
}
