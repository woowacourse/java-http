package org.apache.coyote.http11.util;

import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.coyote.http11.request.RequestCookies;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseCookie;

public final class SessionSupport {

    private SessionSupport() {}

    public static HttpSession findSessionOrCreate(final Manager manager,
                                                  final RequestCookies requestCookies,
                                                  final HttpResponse response) throws IOException {
        HttpSession session = manager.findSession(requestCookies.getCookie("JSESSIONID"));
        if (session == null) {
            session = Session.create(manager);
            final var sessionCookie = new ResponseCookie("JSESSIONID", session.getId());
            response.addHeader("Set-Cookie", sessionCookie.toHeaderString());
        }
        return session;
    }

    public static void rotateSessionAfterLogin(final Manager manager,
                                               final HttpSession previousSession,
                                               final User authenticatedUser,
                                               final HttpResponse response) {
        final var newSession = Session.create(manager);
        newSession.setAttribute("user", authenticatedUser);

        previousSession.invalidate();

        final var rotatedCookie = new ResponseCookie("JSESSIONID", newSession.getId());
        response.addHeader("Set-Cookie", rotatedCookie.toHeaderString());
    }
}
