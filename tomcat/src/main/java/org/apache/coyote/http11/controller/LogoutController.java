package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;

public class LogoutController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        Session session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.addCookie(Cookie.expiredJSessionId());
        response.sendRedirect("/index.html");
    }
}
