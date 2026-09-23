package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.enums.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginRequestHandler implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        final Map<String, String> headers = new HashMap<>();
        try {
            login(httpRequest, headers);
        } catch (IllegalArgumentException e) {
            headers.put("Location", "/401.html");
            return new HttpResponse("/401.html", HttpStatus.UNAUTHORIZED, headers);
        }
        headers.put("Location", "/index.html");
        return new HttpResponse("/index.html", HttpStatus.FOUND, headers);
    }

    private void login(HttpRequest httpRequest, Map<String, String> headers) {
        User user = getValidatedUser(httpRequest.params());
        log.info("user: {}", user.toString());

        removeOldSession(httpRequest);

        String sessionId = saveSession(user);
        headers.put("cookie", sessionId);
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
}
