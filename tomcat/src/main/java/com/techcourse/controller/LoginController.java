package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final Session session = getSession(request, response);
        if (session.getAttribute("user") != null) {
            redirect(response, "/index.html");
            return;
        }

        renderResource(response, "/login.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Session session = getSession(request, response);
        final Map<String, String> parameters = request.getParameters();
        final String account = parameters.get("account");
        final String password = parameters.get("password");

        final Optional<User> optionalUser = account == null
                ? Optional.empty()
                : InMemoryUserRepository.findByAccount(account);
        final boolean loginSuccess = password != null
                && optionalUser.map(user -> user.checkPassword(password)).orElse(false);

        if (loginSuccess) {
            final User user = optionalUser.orElseThrow();
            session.setAttribute("user", user);
            log.info("회원 조회 결과: {}", user);
            redirect(response, "/index.html");
            return;
        }

        redirect(response, 401, "/401.html");
    }

    private Session getSession(final HttpRequest request, final HttpResponse response) {
        final String cookieHeader = request.getHeader("cookie");
        final String sessionId = Cookie.getValue(cookieHeader, "JSESSIONID");
        final Session existingSession = SessionManager.findSession(sessionId);
        if (existingSession != null) {
            return existingSession;
        }

        final Session session = SessionManager.createSession();
        response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId());
        return session;
    }
}
