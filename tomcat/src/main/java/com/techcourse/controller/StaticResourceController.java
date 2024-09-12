package com.techcourse.controller;

import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

public class StaticResourceController implements Controller {

    @Override
    public HttpResponse execute(HttpRequest httpRequest) {
        return new HttpResponse(200, httpRequest.getUrl());
    }
}
