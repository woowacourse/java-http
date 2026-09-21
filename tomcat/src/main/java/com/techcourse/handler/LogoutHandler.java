package com.techcourse.handler;

import org.apache.catalina.SessionManager;
import org.apache.catalina.handler.ResourceHandler;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public class LogoutHandler implements ResourceHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == HttpMethod.GET && request.path().equals("/logout");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        request.cookie("JSESSIONID").ifPresent(SessionManager::remove);
        return HttpResponse.redirect("/index.html");
    }
}
