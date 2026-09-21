package com.techcourse.handler;

import java.io.IOException;
import org.apache.catalina.handler.ResourceHandler;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.StaticResourceBody;

public class LoginPageHandler implements ResourceHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == HttpMethod.GET && request.path().equals("/login");
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        if(request.getSession(false) != null) {
            return HttpResponse.redirect("/");
        }
        return HttpResponse.ok(StaticResourceBody.from("/login.html"));
    }
}
