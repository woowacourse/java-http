package com.techcourse.controller;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.nonNull;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestParams;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private static final String USER_SESSION_ATTRIBUTE = "user";
    private static final Manager SESSION_MANAGER = SessionManager.getInstance();

    @Override
    protected String doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (isLoggedIn(request.cookie())) {
            return "redirect:/index.html";
        }
        return "login";
    }

    @Override
    protected String doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        try {
            final User user = authenticate(request.params());
            final String sessionId = addAuthSession(request.cookie(), user);
            response.setHeader("Set-Cookie", SESSION_COOKIE_NAME + "=" + sessionId);
            return "redirect:/index.html";
        } catch (final IllegalArgumentException e) {
            return "redirect:/login.html?error=" + URLEncoder.encode(e.getMessage(), UTF_8);
        }
    }

    private User authenticate(final RequestParams params) {
        final String account = params.get("account");
        if (account == null) {
            throw new IllegalArgumentException("로그인 정보가 잘못됐다.");
        }
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(params.get("password")))
                .orElseThrow(() -> new IllegalArgumentException("로그인 정보가 잘못됐다."));
    }

    private static String addAuthSession(
            final HttpCookie cookie,
            final User user
    ) throws IOException {
        final String sessionId = cookie.get(SESSION_COOKIE_NAME);
        final HttpSession previousSession = SESSION_MANAGER.findSession(sessionId);
        if (previousSession != null) {
            SESSION_MANAGER.remove(previousSession);
        }

        final HttpSession newSession = new Session(UUID.randomUUID().toString());
        newSession.setAttribute(USER_SESSION_ATTRIBUTE, user);
        SESSION_MANAGER.add(newSession);
        return newSession.getId();
    }

    private boolean isLoggedIn(final HttpCookie cookie) throws IOException {
        final String sessionId = cookie.get(SESSION_COOKIE_NAME);
        final HttpSession session = SESSION_MANAGER.findSession(sessionId);
        return nonNull(session) && nonNull(session.getAttribute(USER_SESSION_ATTRIBUTE));
    }
}
