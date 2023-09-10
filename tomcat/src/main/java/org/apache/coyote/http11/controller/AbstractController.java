package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.controller.exception.UnsupportedHttpMethodException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.requestLine.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isRequestOf(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        if (request.isRequestOf(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        throw new UnsupportedHttpMethodException("지원하지 않는 Http Method 입니다.");
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }
}
