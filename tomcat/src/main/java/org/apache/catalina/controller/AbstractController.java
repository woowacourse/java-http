package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws IOException {
        if ("GET".equals(request.getMethod())) {
            return doGet(request);
        }
        if ("POST".equals(request.getMethod())) {
            return doPost(request);
        }
        return methodNotAllowed();
    }

    protected HttpResponse doGet(HttpRequest request) throws IOException {
        return methodNotAllowed();
    }

    protected HttpResponse doPost(HttpRequest request) throws IOException {
        return methodNotAllowed();
    }

    private HttpResponse methodNotAllowed() {
        HttpResponse response = new HttpResponse();
        response.methodNotAllowed();
        return response;
    }
}
