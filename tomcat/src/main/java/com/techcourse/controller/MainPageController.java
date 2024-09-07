package com.techcourse.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.view.ViewResolver;

public class MainPageController implements Controller{
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.setView(ViewResolver.getView("index.html"));
        response.setStatus(HttpStatus.OK);
        response.setHeaders(HttpHeaders.of(response.getView(), ContentType.findByPath(request.getPath())));
    }
}
