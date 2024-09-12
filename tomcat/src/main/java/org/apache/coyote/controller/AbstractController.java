package org.apache.coyote.controller;

import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public abstract class AbstractController implements Controller {

    protected String path;

    protected AbstractController(String path) {
        this.path = path;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod method = request.getMethod();
        switch (method) {
            case HttpMethod.GET:
                doGet(request, response);
                break;
            case HttpMethod.POST:
                doPost(request, response);
                break;
            default:
                response.setStatusLine(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @Override
    public boolean canControl(HttpRequest request) {
        return request.hasPath(path);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }
}
