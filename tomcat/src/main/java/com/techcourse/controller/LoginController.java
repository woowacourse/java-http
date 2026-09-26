package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    private static final String USER_SESSION_KEY = "user";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {

        final HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        if (getUser(session) == null) {
            return;
        }

        response.sendRedirect(INDEX_PAGE);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {

        final Optional<String> account = request.getParameter(ACCOUNT);
        final Optional<String> password = request.getParameter(PASSWORD);

        final Optional<User> user = account
                .flatMap(InMemoryUserRepository::findByAccount)
                .filter(foundUser -> password.map(foundUser::checkPassword).orElse(false));

        if (user.isEmpty()) {
            log.info("login failed account: {}", account.orElse(""));
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        final User loginUser = user.get();

        final HttpSession session = renewSession(request);

        session.setAttribute(USER_SESSION_KEY, loginUser);
        log.info("login success account: {}", loginUser.getAccount());

        response.sendRedirect(INDEX_PAGE);
    }

    private HttpSession renewSession(final HttpRequest request) {
        final HttpSession existingSession = request.getSession(false);

        if (existingSession != null) {
            existingSession.invalidate();
        }

        return request.getSession();
    }

    private User getUser(final HttpSession session) {
        final Object value = session.getAttribute(USER_SESSION_KEY);

        if (value instanceof User user) {
            return user;
        }

        return null;
    }
}