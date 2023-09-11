package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.HttpUri;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.HttpSession;
import org.apache.coyote.http11.session.SessionManager;

import java.util.Map;
import java.util.Objects;

public class LoginController extends AbstractController {
    private static final SessionManager sessionManager = SessionManager.create();
    private static final String USER = "user";
    private static final String PASSWORD = "password";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final Map<String, String> loginData = request.getBody();
        final User user = InMemoryUserRepository.findByAccount(loginData.get("account"))
                .orElseThrow();
        if (user.checkPassword(loginData.get(PASSWORD))) {
            final HttpCookie newCookie = HttpCookie.create();
            saveSession(newCookie, user);
            response.found(HttpUri.INDEX_HTML.getUri());
            response.setCookie(newCookie.getJSESSIONID());
            return;
        }
        response.ok(HttpUri.UNAUTHORIZED_HTML.getUri());
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final HttpCookie cookie = makeHttpCookie(request);
        if (cookie.hasJSESSIONID()) {
            final String jsessionid = cookie.getJSESSIONID();
            final HttpSession httpSession = sessionManager.findSession(jsessionid);
            if (Objects.isNull(httpSession)) {
                response.ok(HttpUri.LOGIN_HTML.getUri());
                return;
            }
            response.found(HttpUri.INDEX_HTML.getUri());
        }
        response.ok(HttpUri.LOGIN_HTML.getUri());
    }

    private void saveSession(final HttpCookie newCookie, final User user) {
        final HttpSession httpSession = new HttpSession(newCookie.getJSESSIONID());
        httpSession.setAttribute(USER, user);
        sessionManager.add(httpSession);
    }
}
