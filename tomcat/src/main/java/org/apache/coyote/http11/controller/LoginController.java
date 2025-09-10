package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.UUID;
import org.apache.coyote.http11.Http11Cookie;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11Session;
import org.apache.coyote.http11.SessionManager;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        final Http11Cookie cookie = request.getCookie();
        if (cookie.isContainsSessionId() && SessionManager.getInstance().containsSession(cookie.getSessionId())) {
            response.putStatusLine("HTTP/1.1 302 Found");
            response.putHeader("Location", "/index.html");
            response.putBody("");
        } else {
            response.putBody(response.readFileFromClasspath("static/login.html"));
        }
    }

    @Override
    protected void doPost(Http11Request request, Http11Response response) throws Exception {
        try {
            final User user = InMemoryUserRepository.findByAccount(request.getParam("account"))
                    .orElseThrow(() -> new IllegalArgumentException("[ERROR] 회원을 찾을 수 없습니다."));

            if (user.checkPassword(request.getParam("password"))) {
                createSessionAndSetCookie(user, request, response);
                response.sendRedirect("/401.html");
            } else {
                response.sendRedirect("/401.html");
            }
        } catch (IllegalArgumentException e) {
            response.sendRedirect("/401.html");
        }
    }

    private void createSessionAndSetCookie(final User user, final Http11Request request, final Http11Response response) {
        final Http11Cookie cookie = request.getCookie();
        if (cookie.isNotContainsSessionId() || !SessionManager.getInstance().containsSession(cookie.getSessionId())) {
            final String sessionId = UUID.randomUUID().toString();
            final Http11Session session = new Http11Session(sessionId);
            session.setAttribute("user", user);
            SessionManager.getInstance().add(session);
            // TODO: CookieSecurityConfig를 통한 HttpOnly 기본, Secure/SameSite 설정 전략 등 고려하기
            response.putHeader("Set-Cookie", "JSESSIONID=" + sessionId + "; Path=/");
        }
    }
}
