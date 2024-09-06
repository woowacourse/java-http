package com.techcourse.servlet.handler;

import com.techcourse.servlet.Handler;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.view.View;

public class HomePageHandler implements Handler {
    private static final HttpMethod METHOD = HttpMethod.GET;
    private static final String PATH = "/";
    private static final String RESPONSE_CONTENT = "Hello world!";

    @Override
    public boolean support(HttpRequest request) {
        return request.getPath().equals(PATH)
                && request.getMethod() == METHOD;
    }

    @Override
    public View handle(HttpRequest request) {
        return View.createByContent(RESPONSE_CONTENT);
    }
}
