package org.apache.catalina.servlet;

import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.requestLine().method().equals("GET")) {
            doGet(request, response);
        }
        if (request.requestLine().method().equals("POST")) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) { /* NOOP */ }
}
