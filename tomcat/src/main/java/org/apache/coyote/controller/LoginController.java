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

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private static final String USER_SESSION_KEY = "user";
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final var cookie = request.getHeaders().get(HttpHeaderField.COOKIE.getValue());
        final var httpCookie = HttpCookie.parse(cookie);
        if (httpCookie.containsKey(SESSION_COOKIE_NAME)) {
            final var jSessionId = httpCookie.get(SESSION_COOKIE_NAME);
            final var session = sessionManager.findSession(jSessionId);
            response.setHttpStatusCode(HttpStatusCode.FOUND);
            if (session == null) {
                log.warn("유효하지 않은 세션입니다.");
                response.putHeader(HttpHeaderField.LOCATION.getValue(), "401.html");
            } else {
                final var sessionUser = (User) session.getAttribute(USER_SESSION_KEY);
                log.info("이미 로그인 유저 = {}", sessionUser);
                response.putHeader(HttpHeaderField.LOCATION.getValue(), "index.html");
            }
        } else {
            response.setHttpStatusCode(HttpStatusCode.OK);
        }
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final var account = request.getBodyValue("account");
        log.info("account = {}", account);
        try {
            response.setHttpStatusCode(HttpStatusCode.FOUND);
            final var user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
            final var password = request.getBodyValue("password");

            if (!user.checkPassword(password)) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            final var uuid = UUID.randomUUID();
            response.putHeader(HttpHeaderField.SET_COOKIE.getValue(), SESSION_COOKIE_NAME + "=" + uuid);
            response.putHeader(HttpHeaderField.LOCATION.getValue(), "index.html");

            final var session = new Session(uuid.toString());
            session.setAttribute(USER_SESSION_KEY, user);
            sessionManager.add(session);

        } catch (final IllegalArgumentException e) {
            response.putHeader(HttpHeaderField.LOCATION.getValue(), "401.html");
            log.warn(e.getMessage());
        }
    }
}
