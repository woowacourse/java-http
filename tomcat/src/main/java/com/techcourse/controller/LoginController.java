package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Request;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponses;

public class LoginController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String USER = "user";

    private static final String LOGIN_PAGE = "/login.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    @Override
    protected HttpResponse doGet(final Request request) throws Exception {
        boolean isLoggedIn = request.findSession()
                .map(session -> session.getAttribute(USER))
                .isPresent();

        if (isLoggedIn) {
            return HttpResponses.redirect(INDEX_PAGE);
        }

        return HttpResponses.render(LOGIN_PAGE);
    }

    @Override
    protected HttpResponse doPost(final Request request) {
        return findUser(request.body())
                .map(user -> loginSuccess(request, user))
                .orElseGet(() -> HttpResponses.redirect(UNAUTHORIZED_PAGE));
    }

    private Optional<User> findUser(final Map<String, String> body) {
        final String account = body.get(ACCOUNT);
        final String password = body.get(PASSWORD);

        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private HttpResponse loginSuccess(final Request request, final User user) {
        final Session session = request.createSession();
        session.setAttribute(USER, user);

        return HttpResponses.redirect(INDEX_PAGE);
    }
}
