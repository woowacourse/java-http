package com.techcourse.controller;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.isMethod(HttpMethod.GET)) {
            doGet(httpRequest, httpResponse);
        }
        if (httpRequest.isMethod(HttpMethod.POST)) {
            doPost(httpRequest, httpResponse);
        }

        throw new IllegalArgumentException("유효하지 않은 메소드입니다.");
    }

    abstract protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse);

    abstract protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse);
}
