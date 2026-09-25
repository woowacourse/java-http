package com.techcourse.web.controller;

import com.techcourse.session.Session;
import com.techcourse.session.SessionManager;
import com.techcourse.web.cookie.HttpCookie;
import org.apache.catalina.connector.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

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
