package org.apache.catalina.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String COOKIE = "Cookie";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String SESSION_USER_KEY = "user";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getBodyValue(ACCOUNT_KEY);
        final String password = request.getBodyValue(PASSWORD_KEY);

        final HttpResponse loginResponse = InMemoryUserRepository.findByAccount(account)
                                                                 .filter(user -> user.checkPassword(password))
                                                                 .map(LoginController::loginSuccess)
                                                                 .orElseGet(() -> getRedirectResponse("/401.html"));
        response.copy(loginResponse);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.hasHeaderBy(COOKIE) && request.hasCookieKey(JSESSIONID)) {
            final String jsessionid = request.getCookieValue(JSESSIONID);
            final Session session = SessionManager.findSession(jsessionid);
            final User user = (User) session.getAttribute(SESSION_USER_KEY);

            final HttpResponse loginResponse = getLoginResponse(user.getAccount(), user.getPassword());
            response.copy(loginResponse);

            return;
        }

        if (request.hasQueryString()) {
            final HttpResponse loginResponse = getLoginResponse(
                    request.getQueryValue(ACCOUNT_KEY),
                    request.getQueryValue(PASSWORD_KEY)
            );
            response.copy(loginResponse);

            return;
        }

        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, StatusCode.OK);
        final ContentType contentType = ContentType.HTML;
        final String responseBody = getFileToResponseBody("/login.html");

        response.copy(HttpResponse.of(statusLine, contentType, responseBody));
    }

    private static HttpResponse getLoginResponse(final String account, final String password) {
        return InMemoryUserRepository.findByAccount(account)
                                     .filter(user -> user.checkPassword(password))
                                     .map(LoginController::loginSuccess)
                                     .orElseGet(() -> getRedirectResponse("/401.html"));
    }

    private static HttpResponse loginSuccess(final User user) {
        log.info(user.toString());

        final HttpResponse httpResponse = getRedirectResponse("/index.html");

        final Session session = generateJsession(user);
        httpResponse.setCookie(session.getId());

        return httpResponse;
    }

    private static Session generateJsession(final User user) {
        final var uuid = UUID.randomUUID().toString();
        final var session = new Session(uuid);
        session.addAttribute(SESSION_USER_KEY, user);
        SessionManager.add(session);
        return session;
    }
}
