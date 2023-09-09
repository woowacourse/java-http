package org.apache.coyote.controller;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getMethod().equals(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }

        if (request.getMethod().equals(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }

        throw new IllegalArgumentException("지원하지 않는 메서드 입니다.");
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
