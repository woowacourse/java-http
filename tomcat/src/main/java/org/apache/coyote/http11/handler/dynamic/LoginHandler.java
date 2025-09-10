package org.apache.coyote.http11.handler.dynamic;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.session.HttpSessionImpl;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.statics.util.StaticResourceUtils;
import org.apache.coyote.http11.request.dto.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.cookie.CookieUtils;
import org.apache.coyote.http11.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private static final String JSESSIONID = "JSESSIONID";

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.path().startsWith("/login");
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        switch (request.method().toUpperCase()) {
            case "GET"  -> handleGet(request, response);
            case "POST" -> handlePost(request, response);
            default     -> StaticResourceUtils.serve(response, "404.html", HttpStatus.NOT_FOUND);
        }
    }

    private void handleGet(HttpRequest request, HttpResponse response) throws IOException {
        boolean loggedIn = CookieUtils.getCookie(request, JSESSIONID)
                .flatMap(sessionId -> {
                    try {
                        return Optional.ofNullable(SessionManager.getInstance().findSession(sessionId));
                    } catch (IOException e) {
                        return Optional.empty();
                    }
                })
                .map(session -> (User) session.getAttribute("user"))
                .isPresent();

        if (loggedIn) {
            response.sendRedirect("/index.html");
        } else {
            StaticResourceUtils.serve(response, "/login", HttpStatus.OK);
        }
    }

    private void handlePost(HttpRequest request, HttpResponse response) throws IOException {
        String account = request.getParam("account");
        String password = request.getParam("password");

        if (account == null || password == null) {
            StaticResourceUtils.serve(response, "401.html", HttpStatus.UNAUTHORIZED);
            return;
        }

        try {
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + account));
            if (!user.checkPassword(password)) {
                StaticResourceUtils.serve(response, "401.html", HttpStatus.UNAUTHORIZED);
                return;
            }

            HttpSessionImpl session = SessionManager.getInstance().createSession(null);
            session.setAttribute("user", user);
            Cookie sessionCookie = new Cookie(JSESSIONID, session.getId())
                    .httpOnly(true)
                    .path("/")
                    .maxAge(1800);
            response.cookie(sessionCookie);
            response.sendRedirect("/index.html");
            log.info("로그인 성공: {}", account);
        } catch (IllegalArgumentException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            StaticResourceUtils.serve(response, "401.html", HttpStatus.UNAUTHORIZED);
        }
    }
}
