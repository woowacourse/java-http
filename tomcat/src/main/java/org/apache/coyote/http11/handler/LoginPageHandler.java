package org.apache.coyote.http11.handler;

import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.enums.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class LoginPageHandler implements RequestHandler {

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        final Map<String, String> headers = new HashMap<>();

        if (isLoggedIn(httpRequest)) {
            headers.put("Location", "/index.html");
            return new HttpResponse("/index.html", HttpStatus.FOUND, headers);
        }

        return new HttpResponse("/login", HttpStatus.OK, headers);
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
