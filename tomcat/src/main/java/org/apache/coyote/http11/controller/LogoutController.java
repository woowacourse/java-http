package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.Http11Cookie;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.SessionManager;

public class LogoutController extends AbstractController {

    private static final String JSESSIONID = "JSESSIONID";

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        final Http11Cookie cookie = request.getCookie();
        if (cookie.containsKey(JSESSIONID)) {
            SessionManager.getInstance().remove(cookie.get(JSESSIONID));
        }
        response.putHeader("Set-Cookie", "JSESSIONID=; Path=/; Max-Age=0");
        response.sendRedirect("/index.html");
    }
}
