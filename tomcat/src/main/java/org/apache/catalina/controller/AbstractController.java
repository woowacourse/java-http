package org.apache.catalina.controller;


import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        switch (request.getMethod()) {
            case GET:
                doGet(request, response);
                break;
            case POST:
                doPost(request, response);
                break;
            default:
                response.setStatus(HttpStatusCode.METHOD_NOT_ALLOWED);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        // NOP
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        // NOP
    }

    public abstract String matchedPath();
}
