package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.ViewResolver;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.StatusCode;

public class StaticController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String uri = request.getUri();
        response.setBody(ViewResolver.getInstance().resolveViewName(uri));
        response.setStatus(StatusCode.OK);

        if (uri.endsWith("css")) {
            response.addHeader("Content-Type", "text/css;charset=utf-8");
            return;
        }
        if (uri.endsWith("js")) {
            response.addHeader("Content-Type", "text/javascript;charset=utf-8");
            return;
        }
        if (uri.endsWith("svg")) {
            response.addHeader("Content-Type", "image/svg+xml");
            return;
        }
        response.addHeader("Content-Type", "text/html;charset=utf-8");
    }
}
