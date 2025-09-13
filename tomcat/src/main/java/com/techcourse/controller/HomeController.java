package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class HomeController extends AbstractController {

    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return HttpResponse.ok("Hello world!", TEXT_HTML_CHARSET_UTF_8);
    }
}
