package org.apache.catalina.servlet.adapter;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import static org.apache.coyote.http11.request.start.HttpMethod.*;

public abstract class AbstractHandler implements Handler {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.getHttpMethod().equals(GET)) {
            doGet(request, response);
            return;
        }
        if (request.getHttpMethod().equals(POST)) {
            doPost(request, response);
            return;
        }
        throw new IllegalArgumentException("잘못된 요청입니다.");
    }

    protected void doPost(HttpRequest request, HttpResponse response) {}

    protected void doGet(HttpRequest request, HttpResponse response) {}
}