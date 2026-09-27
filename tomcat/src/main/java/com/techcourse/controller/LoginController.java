package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.enums.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatus(HttpStatus.FOUND);
        String location = "/index.html";

        try {
            login(request, response);
        } catch (IllegalArgumentException e) {
            location = "/401.html";
            response.setStatus(HttpStatus.SEE_OTHER);
        }

        response.addHeader("Location", location);
    }

    private void login(HttpRequest httpRequest, HttpResponse response) {
        User user = getValidatedUser(httpRequest.params());
        log.info("user: {}", user.toString());

        removeOldSession(httpRequest);

        String sessionId = saveSession(user);
        response.addHeader("Set-Cookie", "JSESSIONID=" + sessionId);
    }

    private User getValidatedUser(Map<String, String> paramsMap) {
        User user = InMemoryUserRepository.findByAccount(paramsMap.get("account"))
                .orElseThrow(() -> {
                    log.info("로그인 실패: 조건을 만족하는 회원 없음");
                    return new IllegalArgumentException("회원 없음");
                });

        if (!user.checkPassword(paramsMap.get("password"))) {
            log.info("로그인 실패: 비밀번호 불일치");
            throw new IllegalArgumentException("비밀번호 불일치");
        }
        return user;
    }

    private void removeOldSession(HttpRequest httpRequest) {
        final HttpCookie cookie = new HttpCookie(
                httpRequest.headers().getOrDefault("cookie", "")
        );

        try {
            final String sessionId = cookie.getSessionId();
            final SessionManager sessionManager = SessionManager.getInstance();

            if (sessionManager.isExistSession(sessionId)) {
                sessionManager.remove(sessionManager.findSession(sessionId));
            }
        } catch (IllegalArgumentException ignored) {
            // 제거할 기존 세션이 없음
        }
    }

    private String saveSession(User user) {
        String sessionId = UUID.randomUUID().toString();

        Session session = new Session(sessionId);
        session.setAttribute("user", user);

        SessionManager.getInstance().add(session);
        return sessionId;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception{
        response.setStatus(HttpStatus.OK);

        if (isLoggedIn(request)) {
            response.setStatus(HttpStatus.FOUND);
            response.addHeader("Location", "/index.html");
        }
    }

    private boolean isLoggedIn(HttpRequest httpRequest) {
        final HttpCookie cookie = new HttpCookie(
                httpRequest.headers().getOrDefault("cookie", "")
        );

        try {
            final String sessionId = cookie.getSessionId();
            final SessionManager sessionManager = SessionManager.getInstance();

            if (!sessionManager.isExistSession(sessionId)) {
                return false;
            }

            final Session session = sessionManager.findSession(sessionId);
            return getUser(session) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private User getUser(Session session) {
        return (User) session.getAttribute("user");
    }
}
