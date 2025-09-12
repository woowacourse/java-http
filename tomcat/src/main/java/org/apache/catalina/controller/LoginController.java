package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;
import org.apache.coyote.util.UrlEncodedFormParser;

public class LoginController extends AbstractController {

    private static final String SESSION_COOKIE_KEY = "JSESSIONID";

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        if (isUserAlreadyLoggedIn(request)) {
            response.sendRedirection("/index.html");
            return;
        }
        response.sendResource("/login.html");
    }

    @Override
    protected void doPost(final Request request, final Response response) throws Exception {
        final Map<String, String> formData = UrlEncodedFormParser.parse(request.getMessageBody());
        final String account = formData.get("account");
        final String password = formData.get("password");
        if (account == null || password == null) {
            response.sendRedirection("/401.html");
            return;
        }

        final boolean loginSuccess = InMemoryUserRepository.findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);

        if (loginSuccess) {
            handleSuccessfulLogin(account, response);
        } else {
            response.sendRedirection("/401.html");
        }
    }

    private void handleSuccessfulLogin(final String account, final Response response) throws IOException {
        final String sessionId = UUID.randomUUID().toString();
        final Session session = new Session(sessionId);
        session.setAttribute("user", InMemoryUserRepository.findByAccount(account).orElseThrow());
        SessionManager.getInstance().add(session);

        final Cookies responseCookies = new Cookies(Map.of(SESSION_COOKIE_KEY, sessionId));
        response.addCookies(responseCookies);

        response.sendRedirection("/index.html");
    }

    private boolean isUserAlreadyLoggedIn(final Request request) {
        final Cookies cookies = request.getCookies();
        if (cookies == null || !cookies.hasValue(SESSION_COOKIE_KEY)) {
            return false;
        }

        final String sessionId = cookies.getValue(SESSION_COOKIE_KEY);
        return SessionManager.getInstance().contains(sessionId);
    }
}
