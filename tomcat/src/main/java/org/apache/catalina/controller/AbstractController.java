package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod method = request.getMethod();
        if (method == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        if (method == HttpMethod.POST) {
            doPost(request, response);
            return;
        }
        methodNotAllowed(response);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        methodNotAllowed(response);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        methodNotAllowed(response);
    }

    private void methodNotAllowed(HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
