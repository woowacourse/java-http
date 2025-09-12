package org.apache.catalina.controller;

import org.apache.catalina.Controller;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final Request request, final Response response) throws Exception {
        final String method = request.getMethod();
        switch (method) {
            case "GET" -> doGet(request, response);
            case "POST" -> doPost(request, response);
            default -> throw new IllegalArgumentException("처리할 수 없는 HTTP Method 입니다.");
        }
    }

    protected void doGet(final Request request, final Response response) throws Exception { /* NOOP */ }

    protected void doPost(final Request request, final Response response) throws Exception { /* NOOP */ }
}
