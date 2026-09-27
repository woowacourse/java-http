package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    protected static final String JSESSIONID = "JSESSIONID";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.getMethod().equals("POST")) {
            doPost(request, response);
            return;
        }
        if (request.getMethod().equals("GET")) {
            doGet(request, response);
        }
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
    }

    protected void setOkResponse(final HttpResponse response, final String contentType, final byte[] body) {
        response.ok(contentType, body);
    }

    protected void setRedirectResponse(final HttpResponse response, final String location, final String setCookie) {
        response.redirect(location, setCookie);
    }
}
