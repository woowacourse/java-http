package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod method = request.getMethod();

        if (method.isGet()) {
            doGet(request, response);
        }

        if (method.isPost()) {
            doPost(request, response);
        }
    }

    protected abstract void doPost(HttpRequest request, HttpResponse response) throws Exception;

    protected abstract void doGet(HttpRequest request, HttpResponse response) throws Exception;
}
