package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.Session;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(final Request request, final Response response) {
        final Session session = request.getSession();
        final Optional<String> account = request.getParameter("account");

        if (account.isEmpty()) {
            response.redirect("/login.html");
        }

        final Optional<User> maybeUser = InMemoryUserRepository.findByAccount(account.get());
        if (maybeUser.isEmpty()) {
            response.responseUnauthorized();
            return;
        }
        final User findUser = maybeUser.get();
        final Optional<String> password = request.getParameter("password");
        if (password.isEmpty() || !findUser.checkPassword(password.get())) {
            response.responseUnauthorized();
            return;
        }
        log.info("user: {}", findUser);

        session.setAttribute("user", findUser);

        response.redirect("/index.html");
    }

    @Override
    protected void doGet(final Request request, final Response response) {
        final Session session = request.getSession();
        final User user = (User) session.getAttribute("user");
        if (user != null) {
            response.redirect("/index.html");
            return;
        }
        response.setStatusCode(StatusCode.CREATED);
        response.writeStaticResource("/login.html");
    }
}
