package org.apache.catalina.controller;

import org.apache.catalina.exception.HttpRequestMethodNotSupportedException;
import org.apache.coyote.http11.http.request.dto.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.hasGet()) {
            doGet(request, response);
            return;
        }
        if (request.hasPost()) {
            doPost(request, response);
            return;
        }
        throw new HttpRequestMethodNotSupportedException("Request method" + request.method() + "not supported");
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new HttpRequestMethodNotSupportedException("Request method" + request.method() + "not supported");
    }
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        throw new HttpRequestMethodNotSupportedException("Request method" + request.method() + "not supported");
    }
}
