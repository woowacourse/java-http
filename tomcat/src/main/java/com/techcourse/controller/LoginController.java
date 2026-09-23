package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.HttpSessionService;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    private static final String USER_SESSION_KEY = "user";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private final HttpSessionService sessionService;

    public LoginController(final HttpSessionService sessionService) {
        this.sessionService = sessionService;
    }


    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {

        final HttpSession session = sessionService.findSession(request);
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

        final String account = request.getParameter(ACCOUNT).orElse(null);

        final String password = request.getParameter(PASSWORD).orElse(null);

        if (account == null || password == null) {
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password));

        if (user.isEmpty()) {
            log.info("login failed account: {}", account);
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        final User loginUser = user.get();

        final HttpSession session = sessionService.replaceSession(request, response);

        session.setAttribute(USER_SESSION_KEY, loginUser);
        log.info("login success account: {}", loginUser.getAccount());

        response.sendRedirect(INDEX_PAGE);
    }

    private User getUser(final HttpSession session) {
        final Object value = session.getAttribute(USER_SESSION_KEY);

        if (value instanceof User user) {
            return user;
        }

        return null;
    }
}