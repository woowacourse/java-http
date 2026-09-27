package com.techcourse.controller;

import java.util.Set;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class HelloController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setBody("text/html;charset=utf-8", "Hello world!");
    }

    @Override
    protected Set<HttpMethod> allowedMethods() {
        return Set.of(HttpMethod.GET);
    }
}
