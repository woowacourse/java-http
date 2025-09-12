package org.apache.catalina.controller;

import org.apache.coyote.http11.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isGet()) {
            doGet(request, response);
            return;
        }
        if (request.isPost()) {
            doPost(request, response);
            return;
        }
        throw new MethodNotAllowedException();
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new MethodNotAllowedException();
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        throw new MethodNotAllowedException();
    }
}
