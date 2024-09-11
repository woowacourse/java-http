package com.techcourse.controller;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.util.StaticResourceReader;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseFile;

public class NotFoundController implements Controller {

    private static final ResponseFile notFoundPage = StaticResourceReader.read("/404.html");

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatusCode.NOT_FOUND)
                .setBody(notFoundPage);
    }
}
