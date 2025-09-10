package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.Http11Cookie;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11Session;
import org.apache.coyote.http11.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        response.putHeader("Content-Type", "text/html; charset=utf-8");
        response.putBody(response.readFileFromClasspath("static/register.html"));
    }

    @Override
    protected void doPost(Http11Request request, Http11Response response) throws Exception {
        final User user = new User(request.getParam("account"), request.getParam("password"), request.getParam("email"));
        InMemoryUserRepository.save(user);
        log.info("User saved: {}", user);
        createSessionAndSetCookie(user, request, response);
        response.putStatusLine("HTTP/1.1 302 Found");
        response.putHeader("Location", "/index.html");
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
