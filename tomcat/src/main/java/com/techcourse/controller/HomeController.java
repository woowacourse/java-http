package com.techcourse.controller;

import org.apache.catalina.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestMethod;

public class HomeController implements Controller {

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.getRequestMethod() == RequestMethod.GET && httpRequest.getRequestUrl()
                .equals("/");
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        httpResponse.ok()
                .write("Hello world!");
    }
}
