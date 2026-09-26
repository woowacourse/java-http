package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String USER = "user";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (isLoggedIn(request)) {
            response.sendRedirect(INDEX_PAGE);
            return;
        }
        StaticResources.serve(LOGIN_PAGE, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        response.sendRedirect(login(request));
    }

    private boolean isLoggedIn(final HttpRequest request) {
        return request.findSession()
                .map(session -> session.getAttribute(USER))
                .isPresent();
    }

    private String login(final HttpRequest request) {
        final Optional<String> account = request.getParameter(ACCOUNT);
        final Optional<String> password = request.getParameter(PASSWORD);
        if (account.isEmpty() || password.isEmpty()) {
            log.info("login parameters are missing");
            return UNAUTHORIZED_PAGE;
        }
        return InMemoryUserRepository.findByAccount(account.get())
                .filter(user -> user.checkPassword(password.get()))
                .map(user -> {
                    log.info("login success. account: {}", user.getAccount());
                    request.getSession().setAttribute(USER, user);
                    return INDEX_PAGE;
                })
                .orElseGet(() -> {
                    log.info("login failed. account: {}", account.get());
                    return UNAUTHORIZED_PAGE;
                });
    }
}
