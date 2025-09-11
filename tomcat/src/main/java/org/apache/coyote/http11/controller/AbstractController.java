package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.http.common.startline.HttpMethod;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.getMethod().equals(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (request.getMethod().equals(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    protected abstract void doGet(HttpRequest request, HttpResponse response);

    protected abstract void doPost(HttpRequest request, HttpResponse response);
}
