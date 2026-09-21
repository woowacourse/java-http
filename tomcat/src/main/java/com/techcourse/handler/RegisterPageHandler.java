package com.techcourse.handler;

import java.io.IOException;
import org.apache.catalina.handler.ResourceHandler;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.StaticResourceBody;

public class RegisterPageHandler implements ResourceHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == HttpMethod.GET && request.path().equals("/register");
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        return HttpResponse.ok(StaticResourceBody.from("/register.html"));
    }
}
