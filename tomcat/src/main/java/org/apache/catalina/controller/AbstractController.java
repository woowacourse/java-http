package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class AbstractController implements Controller {

    private static final String GET = "GET";
    private static final String POST = "POST";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getMethod().equals(GET)) {
            doGet(request, response);
            return;
        }
        if (request.getMethod().equals(POST)) {
            doPost(request, response);
            return;
        }
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {/* NOOP */ }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {/* NOOP */ }
}
