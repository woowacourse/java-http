package org.apache.coyote.http11.util;

import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.RequestCookies;
import org.apache.coyote.http11.ResponseCookie;
import org.apache.coyote.http11.Session;

public final class SessionSupport {

    private SessionSupport() {}

    public static HttpSession findSessionOrCreate(final Manager manager,
                                                  final RequestCookies requestCookies,
                                                  final Map<String, String> responseHeaders) throws IOException {
        var session = manager.findSession(requestCookies.getCookie("JSESSIONID"));
        if (session == null) {
            session = Session.create(manager);
            final var sessionCookie = new ResponseCookie("JSESSIONID", session.getId());
            responseHeaders.put("Set-Cookie", sessionCookie.toHeaderString());
        }
        return session;
    }

    public static void rotateSessionAfterLogin(final Manager manager,
                                               final HttpSession previousSession,
                                               final User authenticatedUser,
                                               final Map<String, String> responseHeaders) {
        final var newSession = Session.create(manager);
        newSession.setAttribute("user", authenticatedUser);

        previousSession.invalidate();

        final var rotatedCookie = new ResponseCookie("JSESSIONID", newSession.getId());
        responseHeaders.put("Set-Cookie", rotatedCookie.toHeaderString());
    }
}
