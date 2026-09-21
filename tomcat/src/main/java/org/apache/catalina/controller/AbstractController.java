package org.apache.catalina.controller;

import org.apache.catalina.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        switch (request.getMethod()) {
            case GET -> doGet(request, response);
            case POST -> doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
