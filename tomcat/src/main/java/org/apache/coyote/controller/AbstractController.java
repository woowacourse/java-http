package org.apache.coyote.controller;

import org.apache.coyote.domain.request.HttpRequest;
import org.apache.coyote.domain.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        if (httpRequest.isGet()) {
            doGet(httpRequest, httpResponse);
            return;
        }
        doPost(httpRequest, httpResponse);
    }

    abstract void doGet(HttpRequest request, HttpResponse response) throws Exception;

    abstract void doPost(HttpRequest request, HttpResponse response) throws Exception;
}
