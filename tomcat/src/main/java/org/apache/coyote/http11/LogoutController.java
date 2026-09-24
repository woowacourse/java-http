package org.apache.coyote.http11;

import org.apache.cookie.HttpCookie;
import org.apache.session.Session;
import org.apache.session.SessionManager;

import java.io.IOException;

public class LogoutController extends AbstractController {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {

    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        handleLogout(request.getHeader("Cookie"));
        response.redirectTo("/login")
                .expiresCookie("JSESSIONID", "/");
    }

    private void handleLogout(String cookie) {
        String jsessionId = HttpCookie.getJsessionId(cookie);
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.findSession(jsessionId);

        if (session != null) {
            session.invalidate();
            sessionManager.remove(jsessionId);
        }
    }
}
