package com.techcourse.controller;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.util.StaticResourceManager;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseFile;

public class ErrorController implements Controller {

    private static final ResponseFile errorPage = StaticResourceManager.read("/500.html");

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .setBody(errorPage);
    }
}
