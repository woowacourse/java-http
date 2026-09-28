package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getMethod().equals("GET")) {
            doGet(request, response);
        } else if (request.getMethod().equals("POST")) {
            doPost(request, response);
        } else {
            response.sendError(HttpStatus.NOT_IMPLEMENTED, "지원하지 않는 HTTP 메서드입니다.");
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        sendMethodNotAllowed(response);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        sendMethodNotAllowed(response);
    }

    protected abstract String getAllowedMethods();

    private void sendMethodNotAllowed(HttpResponse response) {
        response.setHeader("Allow", getAllowedMethods());
        response.sendError(HttpStatus.METHOD_NOT_ALLOWED, "해당 경로에서 허용하지 않는 HTTP 메서드입니다.");
    }
}
