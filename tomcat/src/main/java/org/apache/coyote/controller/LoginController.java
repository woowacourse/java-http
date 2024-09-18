package org.apache.coyote.controller;

import java.util.UUID;

import org.apache.coyote.component.HttpCookie;
import org.apache.coyote.component.HttpHeaderField;
import org.apache.coyote.component.HttpStatusCode;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController extends AbstractController {

    private static final String AUTHENTICATION_COOKIE_NAME = "JSESSIONID";
    private static final String USER_SESSION_KEY = "user";
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final var cookie = request.getHeaders().get(HttpHeaderField.COOKIE.getValue());
        final var httpCookie = HttpCookie.parse(cookie);
        if (httpCookie.containsKey(AUTHENTICATION_COOKIE_NAME)) {
            findUser(response, httpCookie);
            response.setHttpStatusCode(HttpStatusCode.FOUND);
        } else {
            response.setHttpStatusCode(HttpStatusCode.OK);
        }
    }

    private void findUser(final HttpResponse response, final HttpCookie httpCookie) {
        final var jSessionId = httpCookie.get(AUTHENTICATION_COOKIE_NAME);
        final var session = sessionManager.findSession(jSessionId);
        if (session == null) {
            log.warn("유효하지 않은 세션입니다.");
            response.putHeader(HttpHeaderField.LOCATION.getValue(), "401.html");
        } else {
            final var sessionUser = (User) session.getAttribute(USER_SESSION_KEY);
            log.info("이미 로그인 유저 = {}", sessionUser);
            response.putHeader(HttpHeaderField.LOCATION.getValue(), "index.html");
        }
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final var account = request.getBodyValue("account");
        try {
            response.setHttpStatusCode(HttpStatusCode.FOUND);
            final var user = login(request, account);
            final var uuid = UUID.randomUUID();
            response.putHeader(HttpHeaderField.SET_COOKIE.getValue(), AUTHENTICATION_COOKIE_NAME + "=" + uuid);
            response.putHeader(HttpHeaderField.LOCATION.getValue(), "index.html");
            saveSession(uuid, user);
        } catch (final IllegalArgumentException e) {
            response.putHeader(HttpHeaderField.LOCATION.getValue(), "401.html");
            log.warn(e.getMessage());
        }
    }

    private void saveSession(final UUID uuid, final User user) {
        final var session = new Session(uuid.toString());
        session.setAttribute(USER_SESSION_KEY, user);
        sessionManager.add(session);
    }

    private User login(final HttpRequest request, final String account) {
        final var user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        final var password = request.getBodyValue("password");

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }
}
