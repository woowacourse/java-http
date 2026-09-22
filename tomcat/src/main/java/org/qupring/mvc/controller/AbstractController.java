package org.qupring.mvc.controller;

import org.apache.http.HttpMethod;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getHttpMethod() == HttpMethod.GET) {
            doGet(request, response);
            return;
        }

        if (request.getHttpMethod() == HttpMethod.POST) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        // NOOP
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        // NOOP
    }
}
