package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.constants.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isPost()) {
            doPost(request, response);
        }
        if (request.isGet()) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {}
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {}
}
