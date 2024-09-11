package com.techcourse.servlet.handler;

import com.techcourse.servlet.Handler;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.view.View;

public class RegisterPageHandler implements Handler {
    private static final HttpMethod METHOD = HttpMethod.GET;
    private static final String PATH = "/register";

    @Override
    public boolean support(HttpRequest request) {
        return PATH.equals(request.getPath())
                && METHOD == request.getMethod();
    }

    @Override
    public View handle(HttpRequest request) {
        return View.htmlBuilder()
                .staticResource("/register.html")
                .build();
    }
}
