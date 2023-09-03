package org.apache.coyote.http11.controller;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.apache.coyote.http11.request.Cookies;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.session.SessionManager;

public class SignUpViewController implements Controller {

    private static final String LOCATION_HEADER = "Location";

    @Override
    public Response handle(Request request) {
        if (loggedIn(request)) {
            return Response.status(302)
                .addHeader(LOCATION_HEADER, "/index.html")
                .build();
        }
        Response<Object> response = Response.status(200).build();
        response.responseView("/register.html");
        return response;
    }

    private boolean loggedIn(Request request) {
        Optional<Cookies> cookie = request.getRequestHeaders().getCookie();
        if (cookie.isPresent()) {
            return checkSession(cookie.get());
        }
        return false;
    }

    private boolean checkSession(Cookies cookies) {
        Optional<String> sessionCookie = cookies.getSessionCookie();
        if (sessionCookie.isPresent()) {
            String sessionId = sessionCookie.get();
            HttpSession session = SessionManager.getInstance().findSession(sessionId);
            return session != null;
        }
        return false;
    }
}
