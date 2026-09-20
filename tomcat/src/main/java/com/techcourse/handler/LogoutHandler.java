package com.techcourse.handler;

import org.apache.catalina.SessionManager;
import org.apache.catalina.handler.ResourceHandler;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpServletRequest;
import org.apache.coyote.http.HttpServletResponse;

public class LogoutHandler implements ResourceHandler {

    @Override
    public boolean canHandle(HttpServletRequest request) {
        return request.method() == HttpMethod.GET && request.path().equals("/logout");
    }

    @Override
    public HttpServletResponse handle(HttpServletRequest request) {
        request.cookie("JSESSIONID").ifPresent(SessionManager::remove);
        return HttpServletResponse.redirect("/index.html");
    }
}
