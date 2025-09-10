package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.Http11Cookie;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.SessionManager;

public class LogoutController extends AbstractController {

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        final Http11Cookie cookie = request.getCookie();
        if (cookie.isContainsSessionId()) {
            SessionManager.getInstance().remove(cookie.getSessionId());
        }
        response.putHeader("Set-Cookie", "JSESSIONID=; Path=/; Max-Age=0");
        response.sendRedirect("/index.html");
    }
}
