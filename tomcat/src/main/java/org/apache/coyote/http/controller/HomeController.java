package org.apache.coyote.http.controller;

import org.apache.coyote.http.cookie.HttpCookie;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class HomeController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        final var session = request.getSession(true);
        if (!request.getCookies().hasJSessionId()) {
            return HttpResponse.okWithCookie("Hello world!", "text/html", HttpCookie.JSESSIONID, session.getId());
        }
        return HttpResponse.ok("Hello world!", "text/html");
    }
}
