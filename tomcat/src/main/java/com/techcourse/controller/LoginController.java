package com.techcourse.controller;

import com.techcourse.util.ResourceParser;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.httpResponse.HttpStatus;

public class LoginController extends AbstractController {

    private static final String LOGIN_PATH = "/login.html";

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws Exception {
        return HttpResponse.status(HttpStatus.OK)
                .build(LOGIN_PATH, ResourceParser.parse(LOGIN_PATH));
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) throws Exception {
        return super.doPost(httpRequest);
    }
}
